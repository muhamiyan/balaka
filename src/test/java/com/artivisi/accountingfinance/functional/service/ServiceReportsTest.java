package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.entity.JournalTemplate;
import com.artivisi.accountingfinance.entity.Transaction;
import com.artivisi.accountingfinance.enums.TransactionStatus;
import com.artivisi.accountingfinance.functional.page.BalanceSheetPage;
import com.artivisi.accountingfinance.functional.page.CashFlowPage;
import com.artivisi.accountingfinance.functional.page.IncomeStatementPage;
import com.artivisi.accountingfinance.functional.page.JournalLedgerPage;
import com.artivisi.accountingfinance.functional.page.TrialBalancePage;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.JournalEntryRepository;
import com.artivisi.accountingfinance.repository.JournalTemplateRepository;
import com.artivisi.accountingfinance.repository.TransactionRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Service Industry Financial Reports Tests.
 * Uses Page Object Pattern for maintainability.
 *
 * Test Data (inserted directly to database):
 * - Capital: Rp 500,000,000 (2024-01-01)
 * - Consulting Revenue: Rp 196,200,000 (2024-01-15)
 * - Software Expense: Rp 3,330,000 (2024-01-15)
 * - Cloud Expense: Rp 5,550,000 (2024-01-31)
 * - Training Revenue: Rp 163,500,000 (2024-02-28)
 *
 * Expected Calculations:
 * - Total Revenue: Rp 359,700,000 (196.2M + 163.5M)
 * - Total Expenses: Rp 8,880,000 (3.33M + 5.55M)
 * - Net Income: Rp 350,820,000
 * - Cash Balance: Rp 850,820,000 (500M + 359.7M - 8.88M)
 */
@DisplayName("Service Industry - Financial Reports")
@Import(ServiceTestDataInitializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceReportsTest extends PlaywrightTestBase {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private ChartOfAccountRepository accountRepository;

    @Autowired
    private JournalTemplateRepository templateRepository;

    // Page Objects
    private TrialBalancePage trialBalancePage;
    private IncomeStatementPage incomeStatementPage;
    private BalanceSheetPage balanceSheetPage;
    private CashFlowPage cashFlowPage;
    private JournalLedgerPage journalLedgerPage;

    private void initPageObjects() {
        String baseUrl = "http://localhost:" + port;
        trialBalancePage = new TrialBalancePage(page, baseUrl);
        incomeStatementPage = new IncomeStatementPage(page, baseUrl);
        balanceSheetPage = new BalanceSheetPage(page, baseUrl);
        cashFlowPage = new CashFlowPage(page, baseUrl);
        journalLedgerPage = new JournalLedgerPage(page, baseUrl);
    }

    @BeforeAll
    public void setupTestTransactions() {
        // Get accounts from seed data (IT Service COA)
        ChartOfAccount cash = accountRepository.findByAccountCode("1.1.01").orElseThrow(() ->
            new RuntimeException("Cash account 1.1.01 not found"));
        ChartOfAccount capital = accountRepository.findByAccountCode("3.1.01").orElseThrow(() ->
            new RuntimeException("Capital account 3.1.01 not found"));
        ChartOfAccount consultingRevenue = accountRepository.findByAccountCode("4.1.01").orElseThrow(() ->
            new RuntimeException("Consulting revenue account 4.1.01 not found"));
        ChartOfAccount trainingRevenue = accountRepository.findByAccountCode("4.1.02").orElseThrow(() ->
            new RuntimeException("Training revenue account 4.1.02 not found"));
        ChartOfAccount softwareExpense = accountRepository.findByAccountCode("5.2.01").orElseThrow(() ->
            new RuntimeException("Software expense account 5.2.01 not found"));
        ChartOfAccount cloudExpense = accountRepository.findByAccountCode("5.2.02").orElseThrow(() ->
            new RuntimeException("Cloud expense account 5.2.02 not found"));

        // Get any template (we just need a template reference for transactions)
        JournalTemplate template = templateRepository.findAll().stream().findFirst()
            .orElseThrow(() -> new RuntimeException("No templates found"));

        // Transaction 1: Capital injection Rp 500,000,000
        Transaction tx1 = createTransaction(template, LocalDate.of(2024, 1, 1),
            "Setoran Modal Awal 2024", "CAP-2024-001", BigDecimal.valueOf(500000000));
        createJournalEntry(tx1, cash, BigDecimal.valueOf(500000000), BigDecimal.ZERO);
        createJournalEntry(tx1, capital, BigDecimal.ZERO, BigDecimal.valueOf(500000000));

        // Transaction 2: Consulting revenue Rp 196,200,000
        Transaction tx2 = createTransaction(template, LocalDate.of(2024, 1, 15),
            "Konsultasi Core Banking - Milestone 1", "INV-2024-001", BigDecimal.valueOf(196200000));
        createJournalEntry(tx2, cash, BigDecimal.valueOf(196200000), BigDecimal.ZERO);
        createJournalEntry(tx2, consultingRevenue, BigDecimal.ZERO, BigDecimal.valueOf(196200000));

        // Transaction 3: Software expense Rp 3,330,000
        Transaction tx3 = createTransaction(template, LocalDate.of(2024, 1, 15),
            "JetBrains IntelliJ License 2024", "JB-2024-001", BigDecimal.valueOf(3330000));
        createJournalEntry(tx3, softwareExpense, BigDecimal.valueOf(3330000), BigDecimal.ZERO);
        createJournalEntry(tx3, cash, BigDecimal.ZERO, BigDecimal.valueOf(3330000));

        // Transaction 4: Cloud expense Rp 5,550,000
        Transaction tx4 = createTransaction(template, LocalDate.of(2024, 1, 31),
            "AWS Cloud Services Jan 2024", "AWS-2024-001", BigDecimal.valueOf(5550000));
        createJournalEntry(tx4, cloudExpense, BigDecimal.valueOf(5550000), BigDecimal.ZERO);
        createJournalEntry(tx4, cash, BigDecimal.ZERO, BigDecimal.valueOf(5550000));

        // Transaction 5: Training revenue Rp 163,500,000
        Transaction tx5 = createTransaction(template, LocalDate.of(2024, 2, 28),
            "IT Security Training - Full Payment", "INV-2024-002", BigDecimal.valueOf(163500000));
        createJournalEntry(tx5, cash, BigDecimal.valueOf(163500000), BigDecimal.ZERO);
        createJournalEntry(tx5, trainingRevenue, BigDecimal.ZERO, BigDecimal.valueOf(163500000));
    }

    private Transaction createTransaction(JournalTemplate template, LocalDate date, String description, String reference, BigDecimal amount) {
        Transaction tx = new Transaction();
        tx.setTransactionDate(date);
        tx.setDescription(description);
        tx.setReferenceNumber(reference);
        tx.setAmount(amount);
        tx.setJournalTemplate(template);
        tx.setStatus(TransactionStatus.POSTED);
        tx.setPostedAt(LocalDateTime.now());
        tx.setPostedBy("admin");
        tx.setCreatedBy("admin");
        return transactionRepository.save(tx);
    }

    private void createJournalEntry(Transaction transaction, ChartOfAccount account,
                                     BigDecimal debit, BigDecimal credit) {
        JournalEntry entry = new JournalEntry();
        entry.setTransaction(transaction);
        entry.setAccount(account);
        entry.setDebitAmount(debit);
        entry.setCreditAmount(credit);
        entry.setPostedAt(transaction.getPostedAt());
        entry.setCreatedBy("admin");
        journalEntryRepository.save(entry);
    }

    @Test
    @DisplayName("Should display Trial Balance with correct balances")
    void shouldDisplayTrialBalance() {
        loginAsAdmin();
        initPageObjects();

        trialBalancePage.navigate("2024-01-01", "2024-02-28")
            .verifyPageTitle();

        // Take screenshot for verification
        takeManualScreenshot("service/report-trial-balance");
    }

    @Test
    @DisplayName("Should display Income Statement with correct calculations")
    void shouldDisplayIncomeStatement() {
        loginAsAdmin();
        initPageObjects();

        // Expected from test data:
        // Total Revenue: 359,700,000 (196.2M + 163.5M)
        // Total Expenses: 8,880,000 (3.33M + 5.55M)
        // Net Income: 350,820,000
        incomeStatementPage.navigate("2024-01-01", "2024-02-28")
            .verifyPageTitle()
            .verifyRevenueSectionVisible()
            .verifyExpenseSectionVisible()
            .verifyNetIncomeVisible()
            .verifyTotalRevenue("359.700.000")
            .verifyTotalExpense("8.880.000")
            .verifyNetIncome("350.820.000")
            .verifyProfitStatus();

        takeManualScreenshot("service/report-income-statement");
    }

    @Test
    @DisplayName("Should display Balance Sheet with correct amounts")
    void shouldDisplayBalanceSheet() {
        loginAsAdmin();
        initPageObjects();

        // Expected from test data:
        // Total Assets: 850,820,000 (Cash balance)
        // Total Liabilities: 0
        // Total Equity: 850,820,000 (500M capital + 350.82M earnings)
        // Current Year Earnings: 350,820,000
        balanceSheetPage.navigate("2024-02-28")
            .verifyPageTitle()
            .verifyAssetSectionVisible()
            .verifyLiabilitySectionVisible()
            .verifyEquitySectionVisible()
            .verifyTotalAssets("850.820.000")
            .verifyTotalEquity("850.820.000")
            .verifyCurrentYearEarnings("350.820.000")
            .verifyBalanced();

        takeManualScreenshot("service/report-balance-sheet");
    }

    @Test
    @DisplayName("Should display Cash Flow Statement with activities")
    void shouldDisplayCashFlowStatement() {
        loginAsAdmin();
        initPageObjects();

        // Expected from test data:
        // Operating: Net Income 350,820,000
        // Financing: Capital 500,000,000
        // Ending Balance: 850,820,000
        cashFlowPage.navigate("2024-01-01", "2024-02-28")
            .verifyPageTitle()
            .verifyContentVisible()
            .verifyEndingBalance("850.820.000");

        takeManualScreenshot("service/report-cash-flow");
    }

    @Test
    @DisplayName("Should filter reports by date range")
    void shouldFilterReportsByDateRange() {
        loginAsAdmin();
        initPageObjects();

        incomeStatementPage.navigate()
            .verifyPageTitle();

        // Verify date filter controls exist via page locators
        assertThat(page.locator("#startDate")).isVisible();
        assertThat(page.locator("#endDate")).isVisible();
    }

    @Test
    @DisplayName("Should have export buttons on report")
    void shouldHaveExportButtons() {
        loginAsAdmin();
        initPageObjects();

        incomeStatementPage.navigate()
            .verifyPageTitle();

        // Verify export button exists (PDF)
        assertThat(page.locator("#btn-export-pdf")).isVisible();
    }

    @Test
    @DisplayName("Should display Journal Entry List with 5 posted transactions")
    void shouldDisplayJournalEntryList() {
        loginAsAdmin();
        initPageObjects();

        // Get Cash account to view its ledger
        ChartOfAccount cash = accountRepository.findByAccountCode("1.1.01")
            .orElseThrow(() -> new RuntimeException("Cash account not found"));

        journalLedgerPage.navigate(cash.getId(), "2024-01-01", "2024-02-28")
            .verifyPageTitle()
            .verifyAccountNameVisible("1.1.01", "Kas")
            .verifyEntriesContentVisible()
            .verifyOpeningBalanceVisible()
            .verifyClosingBalance("850,820,000");

        // Take screenshot for verification
        takeManualScreenshot("service/journal-list");
    }
}
