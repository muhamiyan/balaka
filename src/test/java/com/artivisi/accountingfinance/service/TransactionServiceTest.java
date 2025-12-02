package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.dto.FormulaContext;
import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.entity.DraftTransaction;
import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.entity.JournalTemplate;
import com.artivisi.accountingfinance.entity.JournalTemplateLine;
import com.artivisi.accountingfinance.entity.Project;
import com.artivisi.accountingfinance.entity.Transaction;
import com.artivisi.accountingfinance.entity.TransactionAccountMapping;
import com.artivisi.accountingfinance.entity.TransactionSequence;
import com.artivisi.accountingfinance.enums.JournalPosition;
import com.artivisi.accountingfinance.enums.TemplateCategory;
import com.artivisi.accountingfinance.enums.TransactionStatus;
import com.artivisi.accountingfinance.enums.VoidReason;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.JournalEntryRepository;
import com.artivisi.accountingfinance.repository.ProjectRepository;
import com.artivisi.accountingfinance.repository.TransactionRepository;
import com.artivisi.accountingfinance.repository.TransactionSequenceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TransactionService Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionSequenceRepository transactionSequenceRepository;

    @Mock
    private JournalEntryRepository journalEntryRepository;

    @Mock
    private ChartOfAccountRepository chartOfAccountRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private JournalTemplateService journalTemplateService;

    @Mock
    private FormulaEvaluator formulaEvaluator;

    private TransactionService service;

    @BeforeEach
    void setUp() {
        service = new TransactionService(
                transactionRepository,
                transactionSequenceRepository,
                journalEntryRepository,
                chartOfAccountRepository,
                projectRepository,
                journalTemplateService,
                formulaEvaluator);
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should find all transactions")
        void shouldFindAllTransactions() {
            List<Transaction> transactions = List.of(createTransaction(), createTransaction());
            when(transactionRepository.findAll()).thenReturn(transactions);

            List<Transaction> result = service.findAll();

            assertThat(result).hasSize(2);
            verify(transactionRepository).findAll();
        }

        @Test
        @DisplayName("Should find transactions by status")
        void shouldFindByStatus() {
            List<Transaction> transactions = List.of(createTransaction());
            when(transactionRepository.findByStatusOrderByTransactionDateDesc(TransactionStatus.DRAFT))
                    .thenReturn(transactions);

            List<Transaction> result = service.findByStatus(TransactionStatus.DRAFT);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should count transactions by status")
        void shouldCountByStatus() {
            when(transactionRepository.countByStatus(TransactionStatus.POSTED)).thenReturn(5L);

            long count = service.countByStatus(TransactionStatus.POSTED);

            assertThat(count).isEqualTo(5L);
        }

        @Test
        @DisplayName("Should find by filters without project")
        void shouldFindByFiltersWithoutProject() {
            Page<Transaction> page = new PageImpl<>(List.of(createTransaction()));
            when(transactionRepository.findByFilters(any(), any(), isNull(), any(), any(), any()))
                    .thenReturn(page);

            Page<Transaction> result = service.findByFilters(
                    TransactionStatus.POSTED,
                    TemplateCategory.INCOME,
                    LocalDate.now().minusDays(30),
                    LocalDate.now(),
                    PageRequest.of(0, 10));

            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should find by filters with project")
        void shouldFindByFiltersWithProject() {
            UUID projectId = UUID.randomUUID();
            Page<Transaction> page = new PageImpl<>(List.of(createTransaction()));
            when(transactionRepository.findByFilters(any(), any(), eq(projectId), any(), any(), any()))
                    .thenReturn(page);

            Page<Transaction> result = service.findByFilters(
                    TransactionStatus.POSTED,
                    TemplateCategory.INCOME,
                    projectId,
                    LocalDate.now().minusDays(30),
                    LocalDate.now(),
                    PageRequest.of(0, 10));

            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should search transactions")
        void shouldSearchTransactions() {
            Page<Transaction> page = new PageImpl<>(List.of(createTransaction()));
            when(transactionRepository.searchTransactions(anyString(), any()))
                    .thenReturn(page);

            Page<Transaction> result = service.search("consulting", PageRequest.of(0, 10));

            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should find transaction by id")
        void shouldFindById() {
            UUID id = UUID.randomUUID();
            Transaction transaction = createTransaction();
            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

            Transaction result = service.findById(id);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when transaction not found")
        void shouldThrowWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(transactionRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Transaction not found");
        }

        @Test
        @DisplayName("Should find by id with journal entries")
        void shouldFindByIdWithJournalEntries() {
            UUID id = UUID.randomUUID();
            Transaction transaction = createTransaction();
            when(transactionRepository.findByIdWithJournalEntries(id))
                    .thenReturn(Optional.of(transaction));

            Transaction result = service.findByIdWithJournalEntries(id);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should throw when transaction with journal entries not found")
        void shouldThrowWhenWithJournalEntriesNotFound() {
            UUID id = UUID.randomUUID();
            when(transactionRepository.findByIdWithJournalEntries(id))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findByIdWithJournalEntries(id))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Create Transaction")
    class CreateTransactionTests {

        @Test
        @DisplayName("Should create transaction without account mappings")
        void shouldCreateWithoutAccountMappings() {
            JournalTemplate template = createTemplate();
            Transaction transaction = createTransaction();
            transaction.setJournalTemplate(template);

            mockSequenceForTransaction();
            when(journalTemplateService.findByIdWithLines(any())).thenReturn(template);
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.create(transaction, null);

            assertThat(result.getStatus()).isEqualTo(TransactionStatus.DRAFT);
            assertThat(result.getTransactionNumber()).startsWith("TRX");
            verify(journalTemplateService).recordUsage(template.getId());
        }

        @Test
        @DisplayName("Should create transaction with empty account mappings")
        void shouldCreateWithEmptyAccountMappings() {
            JournalTemplate template = createTemplate();
            Transaction transaction = createTransaction();
            transaction.setJournalTemplate(template);

            mockSequenceForTransaction();
            when(journalTemplateService.findByIdWithLines(any())).thenReturn(template);
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.create(transaction, new HashMap<>());

            assertThat(result.getStatus()).isEqualTo(TransactionStatus.DRAFT);
        }

        @Test
        @DisplayName("Should create transaction with account mappings")
        void shouldCreateWithAccountMappings() {
            JournalTemplate template = createTemplateWithLines();
            ChartOfAccount overrideAccount = createAccount("1-1002", "Override Bank");
            Transaction transaction = createTransaction();
            transaction.setJournalTemplate(template);

            UUID lineId = template.getLines().get(0).getId();
            Map<UUID, UUID> mappings = new HashMap<>();
            mappings.put(lineId, overrideAccount.getId());

            mockSequenceForTransaction();
            when(journalTemplateService.findByIdWithLines(any())).thenReturn(template);
            when(chartOfAccountRepository.findById(overrideAccount.getId()))
                    .thenReturn(Optional.of(overrideAccount));
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.create(transaction, mappings);

            assertThat(result.getAccountMappings()).hasSize(1);
        }

        @Test
        @DisplayName("Should throw when override account not found")
        void shouldThrowWhenOverrideAccountNotFound() {
            JournalTemplate template = createTemplateWithLines();
            Transaction transaction = createTransaction();
            transaction.setJournalTemplate(template);

            UUID lineId = template.getLines().get(0).getId();
            UUID fakeAccountId = UUID.randomUUID();
            Map<UUID, UUID> mappings = new HashMap<>();
            mappings.put(lineId, fakeAccountId);

            mockSequenceForTransaction();
            when(journalTemplateService.findByIdWithLines(any())).thenReturn(template);
            when(chartOfAccountRepository.findById(fakeAccountId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(transaction, mappings))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Account not found");
        }

        @Test
        @DisplayName("Should create transaction with project")
        void shouldCreateWithProject() {
            JournalTemplate template = createTemplate();
            Project project = createProject();
            Transaction transaction = createTransaction();
            transaction.setJournalTemplate(template);
            transaction.setProject(project);

            mockSequenceForTransaction();
            when(journalTemplateService.findByIdWithLines(any())).thenReturn(template);
            when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.create(transaction, null);

            assertThat(result.getProject()).isNotNull();
            assertThat(result.getProject().getId()).isEqualTo(project.getId());
        }

        @Test
        @DisplayName("Should throw when project not found")
        void shouldThrowWhenProjectNotFound() {
            JournalTemplate template = createTemplate();
            Project project = createProject();
            Transaction transaction = createTransaction();
            transaction.setJournalTemplate(template);
            transaction.setProject(project);

            mockSequenceForTransaction();
            when(journalTemplateService.findByIdWithLines(any())).thenReturn(template);
            when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(transaction, null))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Project not found");
        }

        @Test
        @DisplayName("Should create transaction without project when project is null")
        void shouldCreateWithoutProjectWhenNull() {
            JournalTemplate template = createTemplate();
            Transaction transaction = createTransaction();
            transaction.setJournalTemplate(template);
            transaction.setProject(null);

            mockSequenceForTransaction();
            when(journalTemplateService.findByIdWithLines(any())).thenReturn(template);
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.create(transaction, null);

            assertThat(result.getProject()).isNull();
        }
    }

    @Nested
    @DisplayName("Update Transaction")
    class UpdateTransactionTests {

        @Test
        @DisplayName("Should update draft transaction")
        void shouldUpdateDraftTransaction() {
            Transaction existing = createTransaction();
            existing.setStatus(TransactionStatus.DRAFT);

            Transaction updateData = new Transaction();
            updateData.setTransactionDate(LocalDate.now().plusDays(1));
            updateData.setAmount(new BigDecimal("20000000"));
            updateData.setDescription("Updated Description");
            updateData.setReferenceNumber("REF-002");
            updateData.setNotes("Updated notes");

            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.update(existing, updateData);

            assertThat(result.getAmount()).isEqualByComparingTo("20000000");
            assertThat(result.getDescription()).isEqualTo("Updated Description");
        }

        @Test
        @DisplayName("Should throw when updating non-draft transaction")
        void shouldThrowWhenUpdatingNonDraft() {
            Transaction existing = createTransaction();
            existing.setStatus(TransactionStatus.POSTED);

            Transaction updateData = new Transaction();

            assertThatThrownBy(() -> service.update(existing, updateData))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only draft transactions can be edited");
        }
    }

    @Nested
    @DisplayName("Post Transaction")
    class PostTransactionTests {

        @Test
        @DisplayName("Should post draft transaction")
        void shouldPostDraftTransaction() {
            UUID id = UUID.randomUUID();
            Transaction transaction = createTransaction();
            transaction.setStatus(TransactionStatus.DRAFT);
            transaction.setAmount(new BigDecimal("1000000"));

            JournalTemplate template = createTemplateWithLines();
            transaction.setJournalTemplate(template);

            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
            when(journalTemplateService.findByIdWithLines(any())).thenReturn(template);
            when(formulaEvaluator.evaluate(anyString(), any())).thenReturn(new BigDecimal("1000000"));
            mockSequenceForJournal();
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.post(id, "admin");

            assertThat(result.getStatus()).isEqualTo(TransactionStatus.POSTED);
            assertThat(result.getPostedBy()).isEqualTo("admin");
            assertThat(result.getPostedAt()).isNotNull();
            assertThat(result.getJournalEntries()).isNotEmpty();
        }

        @Test
        @DisplayName("Should post transaction with custom context")
        void shouldPostWithCustomContext() {
            UUID id = UUID.randomUUID();
            Transaction transaction = createTransaction();
            transaction.setStatus(TransactionStatus.DRAFT);

            JournalTemplate template = createTemplateWithLines();
            transaction.setJournalTemplate(template);

            FormulaContext context = FormulaContext.of(new BigDecimal("2000000"));

            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
            when(journalTemplateService.findByIdWithLines(any())).thenReturn(template);
            when(formulaEvaluator.evaluate(anyString(), any())).thenReturn(new BigDecimal("2000000"));
            mockSequenceForJournal();
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.post(id, "admin", context);

            assertThat(result.getStatus()).isEqualTo(TransactionStatus.POSTED);
        }

        @Test
        @DisplayName("Should throw when posting non-draft transaction")
        void shouldThrowWhenPostingNonDraft() {
            UUID id = UUID.randomUUID();
            Transaction transaction = createTransaction();
            transaction.setStatus(TransactionStatus.POSTED);

            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

            assertThatThrownBy(() -> service.post(id, "admin"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only draft transactions can be posted");
        }

        @Test
        @DisplayName("Should use account overrides when posting")
        void shouldUseAccountOverridesWhenPosting() {
            UUID id = UUID.randomUUID();
            Transaction transaction = createTransaction();
            transaction.setStatus(TransactionStatus.DRAFT);

            JournalTemplate template = createTemplateWithLines();
            transaction.setJournalTemplate(template);

            ChartOfAccount overrideAccount = createAccount("1-1002", "Override Account");
            TransactionAccountMapping mapping = new TransactionAccountMapping();
            mapping.setTemplateLine(template.getLines().get(0));
            mapping.setAccount(overrideAccount);
            transaction.addAccountMapping(mapping);

            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
            when(journalTemplateService.findByIdWithLines(any())).thenReturn(template);
            when(formulaEvaluator.evaluate(anyString(), any())).thenReturn(new BigDecimal("1000000"));
            mockSequenceForJournal();
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.post(id, "admin");

            // Verify override account was used
            JournalEntry firstEntry = result.getJournalEntries().get(0);
            assertThat(firstEntry.getAccount().getAccountCode()).isEqualTo("1-1002");
        }

        @Test
        @DisplayName("Should assign project to journal entries when posting")
        void shouldAssignProjectToJournalEntries() {
            UUID id = UUID.randomUUID();
            Project project = createProject();
            Transaction transaction = createTransaction();
            transaction.setStatus(TransactionStatus.DRAFT);
            transaction.setProject(project);

            JournalTemplate template = createTemplateWithLines();
            transaction.setJournalTemplate(template);

            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
            when(journalTemplateService.findByIdWithLines(any())).thenReturn(template);
            when(formulaEvaluator.evaluate(anyString(), any())).thenReturn(new BigDecimal("1000000"));
            mockSequenceForJournal();
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.post(id, "admin");

            for (JournalEntry entry : result.getJournalEntries()) {
                assertThat(entry.getProject()).isEqualTo(project);
            }
        }

        @Test
        @DisplayName("Should throw when journal is unbalanced")
        void shouldThrowWhenJournalUnbalanced() {
            UUID id = UUID.randomUUID();
            Transaction transaction = createTransaction();
            transaction.setStatus(TransactionStatus.DRAFT);

            JournalTemplate template = createUnbalancedTemplate();
            transaction.setJournalTemplate(template);

            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
            when(journalTemplateService.findByIdWithLines(any())).thenReturn(template);
            // Return different amounts for debit and credit - causes imbalance
            when(formulaEvaluator.evaluate(eq("amount"), any())).thenReturn(new BigDecimal("1000000"));
            when(formulaEvaluator.evaluate(eq("amount * 0.9"), any())).thenReturn(new BigDecimal("900000"));
            mockSequenceForJournal();

            assertThatThrownBy(() -> service.post(id, "admin"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Journal not balanced");
        }
    }

    @Nested
    @DisplayName("Void Transaction")
    class VoidTransactionTests {

        @Test
        @DisplayName("Should void posted transaction")
        void shouldVoidPostedTransaction() {
            UUID id = UUID.randomUUID();
            Transaction transaction = createPostedTransaction();

            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
            mockSequenceForJournal();
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.voidTransaction(id, VoidReason.INPUT_ERROR, "Wrong amount", "admin");

            assertThat(result.getStatus()).isEqualTo(TransactionStatus.VOID);
            assertThat(result.getVoidReason()).isEqualTo(VoidReason.INPUT_ERROR);
            assertThat(result.getVoidNotes()).isEqualTo("Wrong amount");
            assertThat(result.getVoidedBy()).isEqualTo("admin");
            assertThat(result.getVoidedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should create reversal journal entries when voiding")
        void shouldCreateReversalEntriesWhenVoiding() {
            UUID id = UUID.randomUUID();
            Transaction transaction = createPostedTransaction();
            int originalEntryCount = transaction.getJournalEntries().size();

            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
            mockSequenceForJournal();
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.voidTransaction(id, VoidReason.CANCELLED, "Cancelled by customer", "admin");

            // Should have doubled entries (original + reversal)
            assertThat(result.getJournalEntries()).hasSize(originalEntryCount * 2);

            // Check reversal entries
            List<JournalEntry> reversals = result.getJournalEntries().stream()
                    .filter(JournalEntry::getIsReversal)
                    .toList();
            assertThat(reversals).hasSize(originalEntryCount);
        }

        @Test
        @DisplayName("Should throw when voiding non-posted transaction")
        void shouldThrowWhenVoidingNonPosted() {
            UUID id = UUID.randomUUID();
            Transaction transaction = createTransaction();
            transaction.setStatus(TransactionStatus.DRAFT);

            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

            assertThatThrownBy(() -> service.voidTransaction(id, VoidReason.INPUT_ERROR, "Test", "admin"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only posted transactions can be voided");
        }
    }

    @Nested
    @DisplayName("Delete Transaction")
    class DeleteTransactionTests {

        @Test
        @DisplayName("Should delete draft transaction")
        void shouldDeleteDraftTransaction() {
            UUID id = UUID.randomUUID();
            Transaction transaction = createTransaction();
            transaction.setStatus(TransactionStatus.DRAFT);

            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

            service.delete(id);

            verify(transactionRepository).delete(transaction);
        }

        @Test
        @DisplayName("Should throw when deleting non-draft transaction")
        void shouldThrowWhenDeletingNonDraft() {
            UUID id = UUID.randomUUID();
            Transaction transaction = createTransaction();
            transaction.setStatus(TransactionStatus.POSTED);

            when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Only draft transactions can be deleted");
        }
    }

    @Nested
    @DisplayName("Create From Draft")
    class CreateFromDraftTests {

        @Test
        @DisplayName("Should create transaction from draft")
        void shouldCreateFromDraft() {
            UUID templateId = UUID.randomUUID();
            DraftTransaction draft = createDraft();
            JournalTemplate template = createTemplate();

            mockSequenceForTransaction();
            when(journalTemplateService.findByIdWithLines(templateId)).thenReturn(template);
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.createFromDraft(draft, templateId, "Consulting Fee", new BigDecimal("5000000"), "admin");

            assertThat(result.getStatus()).isEqualTo(TransactionStatus.DRAFT);
            assertThat(result.getAmount()).isEqualByComparingTo("5000000");
            assertThat(result.getDescription()).isEqualTo("Consulting Fee");
            assertThat(result.getReferenceNumber()).isEqualTo(draft.getSourceReference());
            verify(journalTemplateService).recordUsage(template.getId());
        }

        @Test
        @DisplayName("Should use draft values when parameters are null")
        void shouldUseDraftValuesWhenParametersNull() {
            UUID templateId = UUID.randomUUID();
            DraftTransaction draft = createDraft();
            draft.setMerchantName("Test Merchant");
            draft.setAmount(new BigDecimal("3000000"));
            JournalTemplate template = createTemplate();

            mockSequenceForTransaction();
            when(journalTemplateService.findByIdWithLines(templateId)).thenReturn(template);
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.createFromDraft(draft, templateId, null, null, "admin");

            assertThat(result.getAmount()).isEqualByComparingTo("3000000");
            assertThat(result.getDescription()).isEqualTo("Test Merchant");
        }

        @Test
        @DisplayName("Should use current date when draft date is null")
        void shouldUseCurrentDateWhenDraftDateNull() {
            UUID templateId = UUID.randomUUID();
            DraftTransaction draft = createDraft();
            draft.setTransactionDate(null);
            JournalTemplate template = createTemplate();

            mockSequenceForTransaction();
            when(journalTemplateService.findByIdWithLines(templateId)).thenReturn(template);
            when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Transaction result = service.createFromDraft(draft, templateId, "Test", new BigDecimal("1000000"), "admin");

            assertThat(result.getTransactionDate()).isEqualTo(LocalDate.now());
        }
    }

    // Helper methods

    private Transaction createTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setTransactionNumber("TRX-2025-00001");
        transaction.setTransactionDate(LocalDate.now());
        transaction.setAmount(new BigDecimal("10000000"));
        transaction.setDescription("Test Transaction");
        transaction.setStatus(TransactionStatus.DRAFT);
        return transaction;
    }

    private Transaction createPostedTransaction() {
        Transaction transaction = createTransaction();
        transaction.setStatus(TransactionStatus.POSTED);
        transaction.setPostedAt(LocalDateTime.now());
        transaction.setPostedBy("admin");

        // Add journal entries
        JournalEntry debitEntry = new JournalEntry();
        debitEntry.setId(UUID.randomUUID());
        debitEntry.setJournalNumber("JE-2025-00001-01");
        debitEntry.setJournalDate(LocalDate.now());
        debitEntry.setAccount(createAccount("1-1001", "Bank"));
        debitEntry.setDebitAmount(new BigDecimal("10000000"));
        debitEntry.setCreditAmount(BigDecimal.ZERO);
        debitEntry.setDescription("Test");
        debitEntry.setIsReversal(false);

        JournalEntry creditEntry = new JournalEntry();
        creditEntry.setId(UUID.randomUUID());
        creditEntry.setJournalNumber("JE-2025-00001-02");
        creditEntry.setJournalDate(LocalDate.now());
        creditEntry.setAccount(createAccount("4-1001", "Revenue"));
        creditEntry.setDebitAmount(BigDecimal.ZERO);
        creditEntry.setCreditAmount(new BigDecimal("10000000"));
        creditEntry.setDescription("Test");
        creditEntry.setIsReversal(false);

        transaction.addJournalEntry(debitEntry);
        transaction.addJournalEntry(creditEntry);

        return transaction;
    }

    private JournalTemplate createTemplate() {
        JournalTemplate template = new JournalTemplate();
        template.setId(UUID.randomUUID());
        template.setTemplateName("Test Template");
        template.setCategory(TemplateCategory.INCOME);
        template.setLines(new ArrayList<>());
        return template;
    }

    private JournalTemplate createTemplateWithLines() {
        JournalTemplate template = createTemplate();

        ChartOfAccount bankAccount = createAccount("1-1001", "Bank");
        ChartOfAccount revenueAccount = createAccount("4-1001", "Revenue");

        JournalTemplateLine debitLine = new JournalTemplateLine();
        debitLine.setId(UUID.randomUUID());
        debitLine.setAccount(bankAccount);
        debitLine.setPosition(JournalPosition.DEBIT);
        debitLine.setFormula("amount");
        debitLine.setLineOrder(1);

        JournalTemplateLine creditLine = new JournalTemplateLine();
        creditLine.setId(UUID.randomUUID());
        creditLine.setAccount(revenueAccount);
        creditLine.setPosition(JournalPosition.CREDIT);
        creditLine.setFormula("amount");
        creditLine.setLineOrder(2);

        template.setLines(List.of(debitLine, creditLine));

        return template;
    }

    private JournalTemplate createUnbalancedTemplate() {
        JournalTemplate template = createTemplate();

        ChartOfAccount bankAccount = createAccount("1-1001", "Bank");
        ChartOfAccount revenueAccount = createAccount("4-1001", "Revenue");

        JournalTemplateLine debitLine = new JournalTemplateLine();
        debitLine.setId(UUID.randomUUID());
        debitLine.setAccount(bankAccount);
        debitLine.setPosition(JournalPosition.DEBIT);
        debitLine.setFormula("amount");
        debitLine.setLineOrder(1);

        JournalTemplateLine creditLine = new JournalTemplateLine();
        creditLine.setId(UUID.randomUUID());
        creditLine.setAccount(revenueAccount);
        creditLine.setPosition(JournalPosition.CREDIT);
        creditLine.setFormula("amount * 0.9");  // Different formula - causes imbalance
        creditLine.setLineOrder(2);

        template.setLines(List.of(debitLine, creditLine));

        return template;
    }

    private ChartOfAccount createAccount(String code, String name) {
        ChartOfAccount account = new ChartOfAccount();
        account.setId(UUID.randomUUID());
        account.setAccountCode(code);
        account.setAccountName(name);
        return account;
    }

    private Project createProject() {
        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setName("Test Project");
        return project;
    }

    private DraftTransaction createDraft() {
        DraftTransaction draft = new DraftTransaction();
        draft.setId(UUID.randomUUID());
        draft.setMerchantName("Test Merchant");
        draft.setAmount(new BigDecimal("1000000"));
        draft.setTransactionDate(LocalDate.now());
        draft.setSourceReference("TG-12345");
        return draft;
    }

    private void mockSequenceForTransaction() {
        TransactionSequence sequence = new TransactionSequence();
        sequence.setSequenceType("TRANSACTION");
        sequence.setPrefix("TRX");
        sequence.setYear(LocalDate.now().getYear());
        sequence.setLastNumber(0);

        when(transactionSequenceRepository.findBySequenceTypeAndYearForUpdate(eq("TRANSACTION"), anyInt()))
                .thenReturn(Optional.of(sequence));
        when(transactionSequenceRepository.save(any())).thenReturn(sequence);
    }

    private void mockSequenceForJournal() {
        TransactionSequence sequence = new TransactionSequence();
        sequence.setSequenceType("JOURNAL");
        sequence.setPrefix("JE");
        sequence.setYear(LocalDate.now().getYear());
        sequence.setLastNumber(0);

        when(transactionSequenceRepository.findBySequenceTypeAndYearForUpdate(eq("JOURNAL"), anyInt()))
                .thenReturn(Optional.of(sequence));
        when(transactionSequenceRepository.save(any())).thenReturn(sequence);
    }
}
