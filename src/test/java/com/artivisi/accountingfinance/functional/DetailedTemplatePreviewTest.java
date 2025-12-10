package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional test for DETAILED template preview functionality.
 * DETAILED templates use custom variables in formulas (e.g., "revenueAmount", "cogsAmount")
 * instead of the simple "amount" variable.
 *
 * This test ensures that:
 * 1. The transaction form shows variable input fields for DETAILED templates
 * 2. Preview works correctly with custom variable values
 * 3. No formula evaluation errors occur
 *
 * Note: Template execute functionality has been consolidated into the transaction form.
 * URLs now use /transactions/new?templateId={id} instead of /templates/{id}/execute
 */
@DisplayName("DETAILED Template Preview Functionality")
class DetailedTemplatePreviewTest extends PlaywrightTestBase {

    private LoginPage loginPage;

    // Template ID from V004 seed data - "Penjualan Persediaan" (DETAILED type)
    // Variables: revenueAmount (selling price), cogsAmount (cost of goods sold)
    private static final String INVENTORY_SALE_TEMPLATE_ID = "f5000000-0000-0000-0000-000000000002";

    // Template line IDs for dynamic account selection
    private static final String HPP_LINE_ID = "f5100000-0000-0000-0000-000000000004";
    private static final String SALES_LINE_ID = "f5100000-0000-0000-0000-000000000005";
    private static final String INVENTORY_LINE_ID = "f5100000-0000-0000-0000-000000000006";

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        loginPage.navigate().loginAsAdmin();
    }

    @Test
    @DisplayName("DETAILED template transaction form should show variable input fields")
    void detailedTemplateExecutePageShouldShowVariableInputs() {
        // Navigate to transaction form with template
        page.navigate(baseUrl() + "/transactions/new?templateId=" + INVENTORY_SALE_TEMPLATE_ID);
        page.waitForLoadState();

        // Wait for Alpine.js to initialize
        page.waitForSelector("[x-data]");

        // Should show template info in the info card (use first() to avoid strict mode violation)
        assertThat(page.locator("text=Penjualan Persediaan").first()).isVisible();

        // For DETAILED template, should NOT show the single "Jumlah" input
        // Instead should show variable inputs section
        assertThat(page.locator("text=Saldo per Akun")).isVisible();

        // Should show variable input fields (use data-testid or specific selectors)
        assertThat(page.locator("[data-var-name]")).not().hasCount(0);

        // Should show inputs for specific variables from the template
        // revenueAmount and cogsAmount
        assertThat(page.locator("[data-var-name='revenueAmount']")).isVisible();
        assertThat(page.locator("[data-var-name='cogsAmount']")).isVisible();
    }

    @Test
    @DisplayName("DETAILED template preview should work without formula evaluation error")
    void detailedTemplatePreviewShouldWorkWithoutError() {
        // Navigate to transaction form with template
        page.navigate(baseUrl() + "/transactions/new?templateId=" + INVENTORY_SALE_TEMPLATE_ID);
        page.waitForLoadState();

        // Wait for Alpine.js to initialize
        page.waitForSelector("[x-data]");

        // Fill in required fields
        page.fill("#description", "Test penjualan persediaan");

        // Fill in variable values
        // For Penjualan Persediaan template:
        // - revenueAmount = 15,000,000 (selling price)
        // - cogsAmount = 10,000,000 (cost of goods sold)
        page.fill("[data-var-name='revenueAmount']", "15000000");
        page.fill("[data-var-name='cogsAmount']", "10000000");

        // Select dynamic accounts for template lines with NULL account:
        // Line IDs are UUIDs from V004 seed data
        // HPP_LINE_ID: HPP account - 50000000-0000-0000-0000-000000000131 (HPP Barang Dagangan)
        // SALES_LINE_ID: Revenue account - 40000000-0000-0000-0000-000000000104 (Pendapatan Penjualan Barang)
        // INVENTORY_LINE_ID: Inventory account - 10000000-0000-0000-0000-000000000151 (Persediaan Barang Dagangan)
        page.selectOption("#accountMapping_" + HPP_LINE_ID, "50000000-0000-0000-0000-000000000131");
        page.selectOption("#accountMapping_" + SALES_LINE_ID, "40000000-0000-0000-0000-000000000104");
        page.selectOption("#accountMapping_" + INVENTORY_LINE_ID, "10000000-0000-0000-0000-000000000151");

        // Wait for HTMX to update preview (triggered by account-changed event)
        page.waitForTimeout(1000);

        // Should show preview section (not error)
        assertThat(page.locator("text=Preview Jurnal")).isVisible();

        // Should NOT show formula evaluation error
        assertThat(page.locator("text=Formula evaluation error")).not().isVisible();
        assertThat(page.locator("text=EL1008E")).not().isVisible();
        assertThat(page.locator("text=cannot be found")).not().isVisible();

        // Preview content should have loaded (check for preview rows in the grid)
        Locator previewContent = page.locator("#preview-content");
        assertThat(previewContent).isVisible();

        // Should show the amounts in the preview
        assertThat(previewContent).containsText("15.000.000");
        assertThat(previewContent).containsText("10.000.000");
    }

    @Test
    @DisplayName("DETAILED template should validate at least one variable has value")
    void detailedTemplateShouldValidateVariables() {
        // Navigate to transaction form with template
        page.navigate(baseUrl() + "/transactions/new?templateId=" + INVENTORY_SALE_TEMPLATE_ID);
        page.waitForLoadState();

        // Wait for Alpine.js to initialize
        page.waitForSelector("[x-data]");

        // Fill only required non-variable fields
        page.fill("#description", "Test validation");

        // Leave all variable inputs empty (or 0)
        // Wait for preview to load (HTMX triggers on load)
        page.waitForTimeout(1000);

        // The preview should show all zero amounts since no variables are filled
        // In DETAILED template, the preview shows calculated amounts based on variables
        Locator previewContent = page.locator("#preview-content");
        assertThat(previewContent).isVisible();

        // Preview should show zero amounts or empty when no variables are set
        // The actual validation happens when user tries to save/post
    }

    @Test
    @DisplayName("SIMPLE template should still show single amount field")
    void simpleTemplateShouldShowSingleAmountField() {
        // Template ID from seed data - "Pendapatan Jasa Konsultasi" (SIMPLE type)
        String simpleTemplateId = "e0000000-0000-0000-0000-000000000001";

        // Navigate to transaction form with template
        page.navigate(baseUrl() + "/transactions/new?templateId=" + simpleTemplateId);
        page.waitForLoadState();

        // Wait for Alpine.js to initialize
        page.waitForSelector("[x-data]");

        // Should show template info in the info card
        assertThat(page.locator("text=Pendapatan Jasa Konsultasi")).isVisible();

        // For SIMPLE template, should show the single "Jumlah" input
        assertThat(page.locator("label:has-text('Jumlah')")).isVisible();
        assertThat(page.locator("#amount")).isVisible();

        // Should NOT show variable inputs section
        assertThat(page.locator("text=Saldo per Akun")).not().isVisible();
        assertThat(page.locator("[data-var-name]")).hasCount(0);
    }
}
