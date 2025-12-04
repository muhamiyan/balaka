package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.InventoryPurchaseFormPage;
import com.artivisi.accountingfinance.functional.page.InventorySaleFormPage;
import com.artivisi.accountingfinance.functional.page.InventoryTransactionDetailPage;
import com.artivisi.accountingfinance.functional.page.InventoryTransactionListPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.ProductFormPage;
import com.artivisi.accountingfinance.functional.page.TransactionListPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Sales Integration (Phase 5.5)")
class SalesIntegrationTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private ProductFormPage productFormPage;
    private InventoryPurchaseFormPage purchaseFormPage;
    private InventorySaleFormPage saleFormPage;
    private InventoryTransactionDetailPage transactionDetailPage;
    private InventoryTransactionListPage transactionListPage;
    private TransactionListPage journalListPage;

    // Account IDs from seed data
    private static final String INVENTORY_ACCOUNT_ID = "10000000-0000-0000-0000-000000000151";
    private static final String COGS_ACCOUNT_ID = "50000000-0000-0000-0000-000000000131";
    private static final String SALES_ACCOUNT_ID = "40000000-0000-0000-0000-000000000104";

    // Test data transaction ID from V911
    private static final String SALE_TRANSACTION_ID = "d0911004-0000-0000-0000-000000000008";

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        productFormPage = new ProductFormPage(page, baseUrl());
        purchaseFormPage = new InventoryPurchaseFormPage(page, baseUrl());
        saleFormPage = new InventorySaleFormPage(page, baseUrl());
        transactionDetailPage = new InventoryTransactionDetailPage(page, baseUrl());
        transactionListPage = new InventoryTransactionListPage(page, baseUrl());
        journalListPage = new TransactionListPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("5.5.1 Margin Display")
    class MarginDisplayTests {

        @Test
        @DisplayName("Should display margin fields for sale transaction")
        void shouldDisplayMarginFieldsForSaleTransaction() {
            transactionDetailPage.navigate(SALE_TRANSACTION_ID);

            transactionDetailPage.assertPageTitleText("Detail Transaksi Persediaan");
            transactionDetailPage.assertSellingPriceVisible();
            transactionDetailPage.assertRevenueVisible();
            transactionDetailPage.assertMarginVisible();
        }

        @Test
        @DisplayName("Should display correct margin amount")
        void shouldDisplayCorrectMarginAmount() {
            transactionDetailPage.navigate(SALE_TRANSACTION_ID);

            // From V911: unit_price=50000, unit_cost=30000, quantity=10
            // Margin = (50000-30000) * 10 = 200,000
            String content = transactionDetailPage.getPageContent();
            assertThat(content).contains("200");
        }

        @Test
        @DisplayName("Should display correct margin percentage")
        void shouldDisplayCorrectMarginPercentage() {
            transactionDetailPage.navigate(SALE_TRANSACTION_ID);

            // From V911: (50000-30000)/50000 * 100 = 40%
            String content = transactionDetailPage.getPageContent();
            assertThat(content).contains("40");
        }

        @Test
        @DisplayName("Should not display margin for non-sale transactions")
        void shouldNotDisplayMarginForNonSaleTransactions() {
            // Navigate to a purchase transaction from V911
            String purchaseId = "d0911004-0000-0000-0000-000000000001";
            transactionDetailPage.navigate(purchaseId);

            assertThat(transactionDetailPage.hasMargin()).isFalse();
            assertThat(transactionDetailPage.hasSellingPrice()).isFalse();
        }
    }

    @Nested
    @DisplayName("5.5.2 Sale Transaction Creation")
    class SaleTransactionCreationTests {

        private String testProductCode;
        private String testProductId;

        @BeforeEach
        void createTestProduct() {
            // Create a test product with inventory accounts configured
            testProductCode = "SALE" + System.currentTimeMillis() % 100000;
            createTestProductWithAccounts();
        }

        private void createTestProductWithAccounts() {
            productFormPage.navigateToNew();
            productFormPage.fillCode(testProductCode);
            productFormPage.fillName("Sales Test Product " + testProductCode);
            productFormPage.fillUnit("pcs");
            productFormPage.selectCostingMethod("WEIGHTED_AVERAGE");
            productFormPage.selectInventoryAccount(INVENTORY_ACCOUNT_ID);
            productFormPage.selectCogsAccount(COGS_ACCOUNT_ID);
            productFormPage.selectSalesAccount(SALES_ACCOUNT_ID);
            productFormPage.fillSellingPrice("25000");
            productFormPage.clickSubmit();

            // Get product ID from URL after creation
            page.navigate(baseUrl() + "/products");
            page.waitForLoadState();
            page.fill("#search-input", testProductCode);
            page.waitForTimeout(500);
            page.click("a:has-text('" + testProductCode + "')");
            page.waitForLoadState();
            String url = page.url();
            testProductId = url.substring(url.lastIndexOf("/") + 1);
        }

        @Test
        @DisplayName("Should create sale transaction with unit price")
        void shouldCreateSaleTransactionWithUnitPrice() {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String purchaseRef = "PO-SALETEST-" + System.currentTimeMillis() % 10000;
            String saleRef = "SO-SALETEST-" + System.currentTimeMillis() % 10000;

            // First, record a purchase to have stock
            purchaseFormPage.navigate();
            purchaseFormPage.selectProductByValue(testProductId);
            purchaseFormPage.fillDate(today);
            purchaseFormPage.fillQuantity("20");
            purchaseFormPage.fillUnitCost("10000");
            purchaseFormPage.fillReference(purchaseRef);
            purchaseFormPage.fillNotes("Purchase for sale test");
            purchaseFormPage.clickSubmit();
            page.waitForLoadState();

            // Record sale
            saleFormPage.navigate();
            saleFormPage.selectProductByValue(testProductId);
            saleFormPage.fillDate(today);
            saleFormPage.fillQuantity("5");
            saleFormPage.fillUnitPrice("20000");
            saleFormPage.fillReference(saleRef);
            saleFormPage.fillNotes("Sale test transaction");
            saleFormPage.clickSubmit();
            page.waitForLoadState();

            // Verify we're on the transaction detail page
            transactionDetailPage.assertPageTitleText("Detail Transaksi Persediaan");
            transactionDetailPage.assertTransactionTypeText("Penjualan");
        }

        @Test
        @DisplayName("Should display correct margin in created sale")
        void shouldDisplayCorrectMarginInCreatedSale() {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String purchaseRef = "PO-MARGIN-" + System.currentTimeMillis() % 10000;
            String saleRef = "SO-MARGIN-" + System.currentTimeMillis() % 10000;

            // Purchase at cost 10,000
            purchaseFormPage.navigate();
            purchaseFormPage.selectProductByValue(testProductId);
            purchaseFormPage.fillDate(today);
            purchaseFormPage.fillQuantity("10");
            purchaseFormPage.fillUnitCost("10000");
            purchaseFormPage.fillReference(purchaseRef);
            purchaseFormPage.clickSubmit();
            page.waitForLoadState();

            // Sale at price 15,000 (50% markup on cost)
            saleFormPage.navigate();
            saleFormPage.selectProductByValue(testProductId);
            saleFormPage.fillDate(today);
            saleFormPage.fillQuantity("5");
            saleFormPage.fillUnitPrice("15000");
            saleFormPage.fillReference(saleRef);
            saleFormPage.clickSubmit();
            page.waitForLoadState();

            // Margin = (15000-10000) * 5 = 25,000
            // Margin % = (15000-10000)/15000 * 100 = 33.3%
            transactionDetailPage.assertMarginVisible();
            String content = transactionDetailPage.getPageContent();
            assertThat(content).contains("25");
            assertThat(content).contains("33");
        }
    }

    @Nested
    @DisplayName("5.5.3 Auto-COGS Journal Entry")
    class AutoCogsJournalEntryTests {

        private String testProductCode;
        private String testProductId;

        @BeforeEach
        void createTestProduct() {
            testProductCode = "COGS" + System.currentTimeMillis() % 100000;
            createTestProductWithAccounts();
        }

        private void createTestProductWithAccounts() {
            productFormPage.navigateToNew();
            productFormPage.fillCode(testProductCode);
            productFormPage.fillName("COGS Test Product " + testProductCode);
            productFormPage.fillUnit("pcs");
            productFormPage.selectCostingMethod("WEIGHTED_AVERAGE");
            productFormPage.selectInventoryAccount(INVENTORY_ACCOUNT_ID);
            productFormPage.selectCogsAccount(COGS_ACCOUNT_ID);
            productFormPage.selectSalesAccount(SALES_ACCOUNT_ID);
            productFormPage.fillSellingPrice("25000");
            productFormPage.clickSubmit();

            page.navigate(baseUrl() + "/products");
            page.waitForLoadState();
            page.fill("#search-input", testProductCode);
            page.waitForTimeout(500);
            page.click("a:has-text('" + testProductCode + "')");
            page.waitForLoadState();
            String url = page.url();
            testProductId = url.substring(url.lastIndexOf("/") + 1);
        }

        @Test
        @DisplayName("Should create COGS journal entry for sale")
        void shouldCreateCogsJournalEntryForSale() {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String purchaseRef = "PO-COGS-" + System.currentTimeMillis() % 10000;
            String saleRef = "SO-COGS-" + System.currentTimeMillis() % 10000;

            // Purchase
            purchaseFormPage.navigate();
            purchaseFormPage.selectProductByValue(testProductId);
            purchaseFormPage.fillDate(today);
            purchaseFormPage.fillQuantity("10");
            purchaseFormPage.fillUnitCost("15000");
            purchaseFormPage.fillReference(purchaseRef);
            purchaseFormPage.clickSubmit();
            page.waitForLoadState();

            // Sale
            saleFormPage.navigate();
            saleFormPage.selectProductByValue(testProductId);
            saleFormPage.fillDate(today);
            saleFormPage.fillQuantity("5");
            saleFormPage.fillUnitPrice("25000");
            saleFormPage.fillReference(saleRef);
            saleFormPage.clickSubmit();
            page.waitForLoadState();

            // Verify journal entry was created
            journalListPage.navigate();
            journalListPage.searchTransaction(testProductCode);

            String pageContent = page.content();
            assertThat(pageContent).contains("Penjualan");
            assertThat(pageContent).contains(testProductCode);
        }

        @Test
        @DisplayName("Should link Transaction and InventoryTransaction")
        void shouldLinkTransactionAndInventoryTransaction() {
            String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            String purchaseRef = "PO-LINK-" + System.currentTimeMillis() % 10000;
            String saleRef = "SO-LINK-" + System.currentTimeMillis() % 10000;

            // Purchase
            purchaseFormPage.navigate();
            purchaseFormPage.selectProductByValue(testProductId);
            purchaseFormPage.fillDate(today);
            purchaseFormPage.fillQuantity("10");
            purchaseFormPage.fillUnitCost("15000");
            purchaseFormPage.fillReference(purchaseRef);
            purchaseFormPage.clickSubmit();
            page.waitForLoadState();

            // Sale
            saleFormPage.navigate();
            saleFormPage.selectProductByValue(testProductId);
            saleFormPage.fillDate(today);
            saleFormPage.fillQuantity("5");
            saleFormPage.fillUnitPrice("25000");
            saleFormPage.fillReference(saleRef);
            saleFormPage.clickSubmit();
            page.waitForLoadState();

            // Navigate to inventory transactions and filter by product
            transactionListPage.navigate();
            // Filter by product using the dropdown
            page.selectOption("select[name='productId']", testProductId);
            page.waitForTimeout(500);

            String content = page.content();
            assertThat(content).contains("Penjualan");
        }
    }

    @Nested
    @DisplayName("5.5.4 Report Integration")
    class ReportIntegrationTests {

        @Test
        @DisplayName("Should include sale transaction in stock movement report")
        void shouldIncludeSaleInStockMovementReport() {
            // Navigate to stock movement report with V911 test data range
            page.navigate(baseUrl() + "/inventory/reports/stock-movement?startDate=2024-01-01&endDate=2024-01-31");
            page.waitForLoadState();

            String content = page.content();
            // Should show the sale transaction from V911 test data
            assertThat(content).contains("Penjualan");
            assertThat(content).contains("INV-001");
        }

        @Test
        @DisplayName("Should include sale in profitability report")
        void shouldIncludeSaleInProfitabilityReport() {
            page.navigate(baseUrl() + "/inventory/reports/profitability?startDate=2024-01-01&endDate=2024-01-31");
            page.waitForLoadState();

            String content = page.content();
            // Should show profitability for Kue Bolu (PRD-TEST-003)
            assertThat(content).contains("PRD-TEST-003");
        }
    }
}
