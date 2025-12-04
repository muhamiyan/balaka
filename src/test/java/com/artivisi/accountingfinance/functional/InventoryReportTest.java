package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.InventoryStockBalanceReportPage;
import com.artivisi.accountingfinance.functional.page.InventoryStockMovementReportPage;
import com.artivisi.accountingfinance.functional.page.InventoryValuationReportPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.ProductProfitabilityReportPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Inventory Reports (Phase 5.3)")
class InventoryReportTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private InventoryStockBalanceReportPage stockBalancePage;
    private InventoryStockMovementReportPage stockMovementPage;
    private InventoryValuationReportPage valuationPage;
    private ProductProfitabilityReportPage profitabilityPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        stockBalancePage = new InventoryStockBalanceReportPage(page, baseUrl());
        stockMovementPage = new InventoryStockMovementReportPage(page, baseUrl());
        valuationPage = new InventoryValuationReportPage(page, baseUrl());
        profitabilityPage = new ProductProfitabilityReportPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("5.3.1 Stock Balance Report")
    class StockBalanceReportTests {

        @Test
        @DisplayName("Should display stock balance report page")
        void shouldDisplayStockBalanceReportPage() {
            stockBalancePage.navigate();

            stockBalancePage.assertPageTitleVisible();
            stockBalancePage.assertPageTitleText("Laporan Saldo Stok");
        }

        @Test
        @DisplayName("Should display stock balance table")
        void shouldDisplayStockBalanceTable() {
            stockBalancePage.navigate();

            stockBalancePage.assertTableVisible();
        }

        @Test
        @DisplayName("Should display export buttons")
        void shouldDisplayExportButtons() {
            stockBalancePage.navigate();

            stockBalancePage.assertExportPdfButtonVisible();
            stockBalancePage.assertExportExcelButtonVisible();
            stockBalancePage.assertPrintButtonVisible();
        }

        @Test
        @DisplayName("Should have correct PDF export URL")
        void shouldHaveCorrectPdfExportUrl() {
            stockBalancePage.navigate();

            String pdfUrl = stockBalancePage.getExportPdfUrl();
            assertThat(pdfUrl).contains("/inventory/reports/stock-balance/export/pdf");
        }

        @Test
        @DisplayName("Should have correct Excel export URL")
        void shouldHaveCorrectExcelExportUrl() {
            stockBalancePage.navigate();

            String excelUrl = stockBalancePage.getExportExcelUrl();
            assertThat(excelUrl).contains("/inventory/reports/stock-balance/export/excel");
        }

        @Test
        @DisplayName("Should display test products in report")
        void shouldDisplayTestProductsInReport() {
            stockBalancePage.navigate();

            assertThat(stockBalancePage.hasProduct("PRD-TEST-001")).isTrue();
            assertThat(stockBalancePage.hasProduct("PRD-TEST-002")).isTrue();
            assertThat(stockBalancePage.hasProduct("PRD-TEST-003")).isTrue();
        }
    }

    @Nested
    @DisplayName("5.3.2 Stock Movement Report")
    class StockMovementReportTests {

        @Test
        @DisplayName("Should display stock movement report page")
        void shouldDisplayStockMovementReportPage() {
            stockMovementPage.navigate();

            stockMovementPage.assertPageTitleVisible();
            stockMovementPage.assertPageTitleText("Laporan Mutasi Stok");
        }

        @Test
        @DisplayName("Should display stock movement table")
        void shouldDisplayStockMovementTable() {
            stockMovementPage.navigateWithParams("2024-01-01", "2024-01-31");

            stockMovementPage.assertTableVisible();
        }

        @Test
        @DisplayName("Should display export buttons")
        void shouldDisplayExportButtons() {
            stockMovementPage.navigate();

            stockMovementPage.assertExportPdfButtonVisible();
            stockMovementPage.assertExportExcelButtonVisible();
            stockMovementPage.assertPrintButtonVisible();
        }

        @Test
        @DisplayName("Should have correct PDF export URL")
        void shouldHaveCorrectPdfExportUrl() {
            stockMovementPage.navigate();

            String pdfUrl = stockMovementPage.getExportPdfUrl();
            assertThat(pdfUrl).contains("/inventory/reports/stock-movement/export/pdf");
        }

        @Test
        @DisplayName("Should have correct Excel export URL")
        void shouldHaveCorrectExcelExportUrl() {
            stockMovementPage.navigate();

            String excelUrl = stockMovementPage.getExportExcelUrl();
            assertThat(excelUrl).contains("/inventory/reports/stock-movement/export/excel");
        }

        @Test
        @DisplayName("Should display transactions in date range")
        void shouldDisplayTransactionsInDateRange() {
            stockMovementPage.navigateWithParams("2024-01-01", "2024-01-31");

            int rowCount = stockMovementPage.getRowCount();
            assertThat(rowCount).isGreaterThanOrEqualTo(8); // 8 transactions in test data
        }
    }

    @Nested
    @DisplayName("5.3.3 Inventory Valuation Report")
    class InventoryValuationReportTests {

        @Test
        @DisplayName("Should display valuation report page")
        void shouldDisplayValuationReportPage() {
            valuationPage.navigate();

            valuationPage.assertPageTitleVisible();
            valuationPage.assertPageTitleText("Laporan Penilaian Persediaan");
        }

        @Test
        @DisplayName("Should display valuation table")
        void shouldDisplayValuationTable() {
            valuationPage.navigate();

            valuationPage.assertTableVisible();
        }

        @Test
        @DisplayName("Should display export buttons")
        void shouldDisplayExportButtons() {
            valuationPage.navigate();

            valuationPage.assertExportPdfButtonVisible();
            valuationPage.assertExportExcelButtonVisible();
            valuationPage.assertPrintButtonVisible();
        }

        @Test
        @DisplayName("Should have correct PDF export URL")
        void shouldHaveCorrectPdfExportUrl() {
            valuationPage.navigate();

            String pdfUrl = valuationPage.getExportPdfUrl();
            assertThat(pdfUrl).contains("/inventory/reports/valuation/export/pdf");
        }

        @Test
        @DisplayName("Should have correct Excel export URL")
        void shouldHaveCorrectExcelExportUrl() {
            valuationPage.navigate();

            String excelUrl = valuationPage.getExportExcelUrl();
            assertThat(excelUrl).contains("/inventory/reports/valuation/export/excel");
        }
    }

    @Nested
    @DisplayName("5.3.4 Product Profitability Report")
    class ProductProfitabilityReportTests {

        @Test
        @DisplayName("Should display product profitability report page")
        void shouldDisplayProductProfitabilityReportPage() {
            profitabilityPage.navigate();

            profitabilityPage.assertPageTitleVisible();
            profitabilityPage.assertPageTitleText("Laporan Profitabilitas Produk");
        }

        @Test
        @DisplayName("Should display export buttons")
        void shouldDisplayExportButtons() {
            profitabilityPage.navigate();

            profitabilityPage.assertExportPdfButtonVisible();
            profitabilityPage.assertExportExcelButtonVisible();
            profitabilityPage.assertPrintButtonVisible();
        }

        @Test
        @DisplayName("Should have correct PDF export URL")
        void shouldHaveCorrectPdfExportUrl() {
            profitabilityPage.navigate();

            String pdfUrl = profitabilityPage.getExportPdfUrl();
            assertThat(pdfUrl).contains("/inventory/reports/profitability/export/pdf");
        }

        @Test
        @DisplayName("Should have correct Excel export URL")
        void shouldHaveCorrectExcelExportUrl() {
            profitabilityPage.navigate();

            String excelUrl = profitabilityPage.getExportExcelUrl();
            assertThat(excelUrl).contains("/inventory/reports/profitability/export/excel");
        }

        @Test
        @DisplayName("Should display profitability data with date range")
        void shouldDisplayProfitabilityDataWithDateRange() {
            profitabilityPage.navigateWithParams("2024-01-01", "2024-01-31");

            profitabilityPage.assertTableVisible();
            // Should have at least 1 product with sales (Kue Bolu)
            int rowCount = profitabilityPage.getRowCount();
            assertThat(rowCount).isGreaterThanOrEqualTo(1);
        }
    }
}
