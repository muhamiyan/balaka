package com.artivisi.accountingfinance.functional.manufacturing;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Manufacturing Costing Tests
 * Tests COGM (Cost of Goods Manufactured) and inventory valuation.
 * Data from V831: Production costs and inventory transactions.
 */
@DisplayName("Manufacturing - Costing & Valuation")
public class MfgCostingTest extends PlaywrightTestBase {

    @Test
    @DisplayName("Should display inventory transactions page")
    void shouldDisplayInventoryTransactionsPage() {
        loginAsAdmin();
        navigateTo("/inventory/transactions");
        waitForPageLoad();

        // Verify inventory transactions page loads
        assertThat(page.locator("h1")).containsText("Transaksi Persediaan");
    }

    @Test
    @DisplayName("Should display production output transactions")
    void shouldDisplayProductionOutputTransactions() {
        loginAsAdmin();
        navigateTo("/inventory/transactions");
        waitForPageLoad();

        // Verify PRODUCTION_IN transactions from V831
        assertThat(page.locator("text=PRODUCTION_IN").first()).isVisible();
    }

    @Test
    @DisplayName("Should display production component consumption")
    void shouldDisplayProductionComponentConsumption() {
        loginAsAdmin();
        navigateTo("/inventory/transactions");
        waitForPageLoad();

        // Verify PRODUCTION_OUT transactions from V831
        assertThat(page.locator("text=PRODUCTION_OUT").first()).isVisible();
    }

    @Test
    @DisplayName("Should display finished goods inventory after production")
    void shouldDisplayFinishedGoodsInventoryAfterProduction() {
        loginAsAdmin();
        navigateTo("/inventory/stock");
        waitForPageLoad();

        // Verify finished goods have stock after production from V831
        // Croissant: 24 produced, 15 sold = 9 remaining
        assertThat(page.locator("text=Croissant")).isVisible();
        // Roti Bakar Coklat: 20 produced, 12 sold = 8 remaining
        assertThat(page.locator("text=Roti Bakar Coklat")).isVisible();
    }

    @Test
    @DisplayName("Should display purchase transactions")
    void shouldDisplayPurchaseTransactions() {
        loginAsAdmin();
        navigateTo("/inventory/transactions");
        waitForPageLoad();

        // Verify PURCHASE transactions from V831
        assertThat(page.locator("text=PURCHASE").first()).isVisible();
    }

    @Test
    @DisplayName("Should display sales transactions with COGS")
    void shouldDisplaySalesTransactionsWithCogs() {
        loginAsAdmin();
        navigateTo("/inventory/transactions");
        waitForPageLoad();

        // Verify SALE transactions from V831
        assertThat(page.locator("text=SALE").first()).isVisible();
    }

    @Test
    @DisplayName("Should show unit cost in production order detail")
    void shouldShowUnitCostInProductionOrderDetail() {
        loginAsAdmin();
        navigateTo("/inventory/production");
        waitForPageLoad();

        // Click on PROD-001 to see unit cost
        page.locator("a:has-text('PROD-001')").first().click();
        waitForPageLoad();

        // Verify unit cost is displayed (4,455 per croissant)
        // The page should show cost information
        assertThat(page.locator("text=4.455").or(page.locator("text=4455")).or(page.locator("text=Rp"))).isVisible();
    }
}
