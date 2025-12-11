package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Inventory Transaction List (/inventory/transactions).
 * Handles inventory transaction display and navigation.
 */
public class InventoryTransactionListPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs
    private static final String PAGE_TITLE = "#page-title";
    private static final String TRANSACTION_TABLE = "#transaction-table";
    private static final String SEARCH_INPUT = "#search-input";
    private static final String TYPE_FILTER = "#type-filter";
    private static final String BTN_NEW_PURCHASE = "#btn-new-purchase";
    private static final String BTN_NEW_SALE = "#btn-new-sale";
    private static final String BTN_NEW_ADJUSTMENT = "#btn-new-adjustment";

    public InventoryTransactionListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public InventoryTransactionListPage navigate() {
        page.navigate(baseUrl + "/inventory/transactions");
        page.waitForLoadState();
        return this;
    }

    public InventoryTransactionListPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Transaksi");
        return this;
    }

    public InventoryTransactionListPage verifyTableVisible() {
        assertThat(page.locator(TRANSACTION_TABLE)).isVisible();
        return this;
    }

    public InventoryTransactionListPage verifyTransactionCount(int expectedCount) {
        assertThat(page.locator(TRANSACTION_TABLE + " tbody tr")).hasCount(expectedCount);
        return this;
    }

    public InventoryTransactionListPage verifyMinimumTransactionCount(int minCount) {
        int count = page.locator(TRANSACTION_TABLE + " tbody tr").count();
        if (count < minCount) {
            throw new AssertionError("Expected at least " + minCount + " transactions, but found " + count);
        }
        return this;
    }

    public InventoryTransactionListPage search(String query) {
        page.locator(SEARCH_INPUT).fill(query);
        page.waitForTimeout(500);
        return this;
    }

    public InventoryTransactionListPage filterByType(String type) {
        page.locator(TYPE_FILTER).selectOption(type);
        page.waitForLoadState();
        return this;
    }

    public InventoryTransactionListPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
