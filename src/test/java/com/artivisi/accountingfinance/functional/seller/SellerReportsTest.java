package com.artivisi.accountingfinance.functional.seller;

import com.artivisi.accountingfinance.functional.page.InventoryReportPage;
import com.artivisi.accountingfinance.functional.page.TrialBalancePage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Online Seller Reports Tests
 * Tests inventory reports and profitability analysis.
 * Uses Page Object Pattern with ID-based locators.
 *
 * Expected Data (after SellerTransactionExecutionTest runs):
 * - 4 Purchase transactions: IP15PRO (10), SGS24 (20), USBC (100), CASE (200)
 * - 4 Sale transactions: IP15PRO (5), SGS24 (8), USBC (30), CASE (50)
 * - 1 Adjustment: USBC (+5)
 *
 * Expected Stock (from expected-inventory.csv):
 * - IP15PRO: 5 pcs @ 15,000,000
 * - SGS24: 12 pcs @ 12,000,000
 * - USBC: 75 pcs @ 25,000
 * - CASE: 150 pcs @ 15,000
 */
@DisplayName("Online Seller - Reports")
@Import(SellerTestDataInitializer.class)
public class SellerReportsTest extends PlaywrightTestBase {

    // Page Objects
    private InventoryReportPage inventoryReportPage;
    private TrialBalancePage trialBalancePage;

    private void initPageObjects() {
        String baseUrl = "http://localhost:" + port;
        inventoryReportPage = new InventoryReportPage(page, baseUrl);
        trialBalancePage = new TrialBalancePage(page, baseUrl);
    }

    @Test
    @DisplayName("Should display inventory stock balance report with 4 products")
    void shouldDisplayStockBalanceReport() {
        loginAsAdmin();
        initPageObjects();

        inventoryReportPage.navigateStockBalance()
            .verifyPageTitle("Saldo Stok")
            .verifyReportTableVisible()
            .verifyProductCount(4);

        takeManualScreenshot("seller/report-stock-balance");
    }

    @Test
    @DisplayName("Should display inventory stock movement report")
    void shouldDisplayStockMovementReport() {
        loginAsAdmin();
        initPageObjects();

        inventoryReportPage.navigateStockMovement()
            .verifyPageTitle("Mutasi Stok")
            .verifyReportTableVisible();

        takeManualScreenshot("seller/report-stock-movement");
    }

    @Test
    @DisplayName("Should display inventory valuation report")
    void shouldDisplayValuationReport() {
        loginAsAdmin();
        initPageObjects();

        inventoryReportPage.navigateValuation()
            .verifyPageTitle("Penilaian")
            .verifyReportTableVisible()
            .verifyProductCount(4);

        takeManualScreenshot("seller/report-valuation");
    }

    @Test
    @DisplayName("Should display product profitability report")
    void shouldDisplayProductProfitabilityReport() {
        loginAsAdmin();
        initPageObjects();

        inventoryReportPage.navigateProfitability()
            .verifyPageTitle("Profitabilitas")
            .verifyReportTableVisible();

        takeManualScreenshot("seller/report-profitability");
    }

    @Test
    @DisplayName("Should display trial balance")
    void shouldDisplayTrialBalance() {
        loginAsAdmin();
        initPageObjects();

        trialBalancePage.navigate()
            .verifyPageTitle();

        takeManualScreenshot("seller/report-trial-balance");
    }
}
