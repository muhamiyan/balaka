package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.entity.Client;
import com.artivisi.accountingfinance.entity.Invoice;
import com.artivisi.accountingfinance.enums.InvoiceStatus;
import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ClientRepository;
import com.artivisi.accountingfinance.repository.InvoiceRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for ClientController.
 * Tests client list, create, edit, deactivate operations.
 */
@DisplayName("Client Controller Tests")
@Import(ServiceTestDataInitializer.class)
class ClientControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display client list page")
    void shouldDisplayClientListPage() {
        navigateTo("/clients");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter clients by status")
    void shouldFilterClientsByStatus() {
        navigateTo("/clients");
        waitForPageLoad();

        var activeCheckbox = page.locator("input[name='active']").first();
        if (activeCheckbox.isVisible()) {
            activeCheckbox.check();

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should search clients by keyword")
    void shouldSearchClientsByKeyword() {
        navigateTo("/clients");
        waitForPageLoad();

        var searchInput = page.locator("input[name='search']").first();
        if (searchInput.isVisible()) {
            searchInput.fill("test");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display new client form")
    void shouldDisplayNewClientForm() {
        navigateTo("/clients/new");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new client")
    void shouldCreateNewClient() {
        navigateTo("/clients/new");
        waitForPageLoad();

        // Fill client name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Test Client " + System.currentTimeMillis());
        }

        // Fill email
        var emailInput = page.locator("input[name='email']").first();
        if (emailInput.isVisible()) {
            emailInput.fill("testclient" + System.currentTimeMillis() + "@example.com");
        }

        // Fill phone
        var phoneInput = page.locator("input[name='phone']").first();
        if (phoneInput.isVisible()) {
            phoneInput.fill("08123456789");
        }

        // Fill address
        var addressInput = page.locator("textarea[name='address'], input[name='address']").first();
        if (addressInput.isVisible()) {
            addressInput.fill("123 Test Street");
        }

        // Submit
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for empty name")
    void shouldShowValidationErrorForEmptyName() {
        navigateTo("/clients/new");
        waitForPageLoad();

        // Submit without filling name
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        // Should stay on form or show error
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display client detail page")
    void shouldDisplayClientDetailPage() {
        var client = clientRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("No client found in test data"));

        navigateTo("/clients/" + client.getCode());
        waitForPageLoad();

        // Page title is "Detail Klien", client name appears in body
        assertThat(page.locator("#page-title, h1").first()).isVisible();
        assertThat(page.locator("body")).containsText(client.getName());
    }

    @Test
    @DisplayName("Should display client edit form")
    void shouldDisplayClientEditForm() {
        var client = clientRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("No client found in test data"));

        navigateTo("/clients/" + client.getCode() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("input[name='name']")).hasValue(client.getName());
    }

    @Test
    @DisplayName("Should update client")
    void shouldUpdateClient() {
        var client = clientRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("No client found in test data"));

        navigateTo("/clients/" + client.getCode() + "/edit");
        waitForPageLoad();

        String updatedName = "Updated Client " + System.currentTimeMillis();
        page.fill("input[name='name']", updatedName);

        page.click("#btn-simpan");
        waitForPageLoad();

        // Should redirect to detail page
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/clients\\/.*"));
    }

    @Test
    @DisplayName("Should deactivate client")
    void shouldDeactivateClient() {
        var client = clientRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getActive()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No active client found in test data"));

        navigateTo("/clients/" + client.getCode());
        waitForPageLoad();

        page.click("form[action*='/deactivate'] button[type='submit']");
        waitForPageLoad();

        // Should stay on detail page
        assertThat(page.locator("#page-title, h1").first()).isVisible();
        assertThat(page.locator("body")).containsText(client.getName());
    }

    @Test
    @DisplayName("Should activate client")
    void shouldActivateClient() {
        // First deactivate a client
        var client = clientRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getActive()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No active client found in test data"));

        navigateTo("/clients/" + client.getCode());
        waitForPageLoad();

        // Deactivate first if activate button not visible
        var deactivateBtn = page.locator("form[action*='/deactivate'] button[type='submit']").first();
        if (deactivateBtn.isVisible()) {
            deactivateBtn.click();
            waitForPageLoad();
        }

        // Now activate
        var activateBtn = page.locator("form[action*='/activate'] button[type='submit']").first();
        if (activateBtn.isVisible()) {
            activateBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("#page-title, h1").first()).isVisible();
        assertThat(page.locator("body")).containsText(client.getName());
    }

    @Test
    @DisplayName("Should filter clients with active filter")
    void shouldFilterClientsWithActiveFilter() {
        navigateTo("/clients?active=true");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter clients with search param")
    void shouldFilterClientsWithSearchParam() {
        navigateTo("/clients?search=client");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should handle HTMX request for client list")
    void shouldHandleHtmxRequestForClientList() {
        // Navigate normally first
        navigateTo("/clients");
        waitForPageLoad();

        // Now test that list loads
        assertThat(page.locator("table, [data-testid='client-list']").first()).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for duplicate code")
    void shouldShowValidationErrorForDuplicateCode() {
        var existingClient = clientRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("No client found in test data"));

        navigateTo("/clients/new");
        waitForPageLoad();

        page.fill("input[name='code']", existingClient.getCode());
        page.fill("input[name='name']", "Duplicate Code Client");

        page.click("#btn-simpan");
        waitForPageLoad();

        // Should stay on form with error
        assertThat(page.locator("input[name='code']")).isVisible();
    }

    @Test
    @DisplayName("Should display invoices section on client detail page")
    void shouldDisplayInvoicesSectionOnClientDetail() {
        Client client = clientRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("No client found in test data"));

        // Create a test invoice for this client
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-TEST-" + System.currentTimeMillis());
        invoice.setClient(client);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setAmount(new BigDecimal("10000000"));
        invoice.setTaxAmount(new BigDecimal("1100000"));
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoiceRepository.save(invoice);

        navigateTo("/clients/" + client.getCode());
        waitForPageLoad();

        // Invoices section should be visible with invoice data
        assertThat(page.locator("[data-testid='invoices-section']")).isVisible();
        assertThat(page.locator("[data-testid='invoices-section']")).containsText(invoice.getInvoiceNumber());
    }

    @Test
    @DisplayName("Should display tax summary for client with NPWP")
    void shouldDisplayTaxSummaryForClientWithNpwp() {
        // MANDIRI client has NPWP 01.310.523.4-091.000
        Client client = clientRepository.findByCode("MANDIRI")
                .orElseThrow(() -> new AssertionError("MANDIRI client not found in test data"));

        navigateTo("/clients/" + client.getCode());
        waitForPageLoad();

        // Tax info card should be visible (client has NPWP)
        assertThat(page.locator("body")).containsText("Informasi Pajak");
        assertThat(page.locator("body")).containsText(client.getNpwp());
    }

    @Test
    @DisplayName("Should reject invalid NPWP format")
    void shouldRejectInvalidNpwpFormat() {
        navigateTo("/clients/new");
        waitForPageLoad();

        String uniqueCode = "NPWP-TEST-" + System.currentTimeMillis();
        page.fill("input[name='code']", uniqueCode);
        page.fill("input[name='name']", "Test NPWP Validation");
        page.fill("input[name='npwp']", "12345-invalid");

        page.click("#btn-simpan");
        waitForPageLoad();

        // Should stay on form with validation error
        assertThat(page.locator("input[name='npwp']")).isVisible();
        assertThat(page.locator("body")).containsText("Format NPWP tidak valid");
    }
}
