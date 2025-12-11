package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Transaction List (/transactions).
 * Handles viewing and filtering transaction list.
 */
public class TransactionListPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs only
    private static final String PAGE_TITLE = "#page-title";
    private static final String TRANSACTIONS_LIST_CONTENT = "#transactions-list-content";
    private static final String FILTER_FORM = "#filter-form";
    private static final String SEARCH_INPUT = "#search-transaksi";
    private static final String FILTER_STATUS = "#filter-status";
    private static final String FILTER_CATEGORY = "#filter-category";
    private static final String FILTER_PROJECT = "#filter-project";
    private static final String BTN_FILTER = "#btn-filter";
    private static final String BTN_TRANSAKSI_BARU = "#btn-transaksi-baru";
    private static final String TRANSACTION_TABLE = "#transaction-table";

    public TransactionListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    /**
     * Navigate to transaction list.
     */
    public TransactionListPage navigate() {
        page.navigate(baseUrl + "/transactions");
        page.waitForLoadState();
        return this;
    }

    /**
     * Verify page title.
     */
    public TransactionListPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Transaksi");
        return this;
    }

    /**
     * Verify content is visible.
     */
    public TransactionListPage verifyContentVisible() {
        assertThat(page.locator(TRANSACTIONS_LIST_CONTENT)).isVisible();
        return this;
    }

    /**
     * Search transactions by text.
     */
    public TransactionListPage search(String query) {
        page.locator(SEARCH_INPUT).fill(query);
        page.locator(BTN_FILTER).click();
        page.waitForLoadState();
        return this;
    }

    /**
     * Filter by status.
     */
    public TransactionListPage filterByStatus(String status) {
        page.locator(FILTER_STATUS).selectOption(status);
        page.waitForLoadState();
        return this;
    }

    /**
     * Filter by category.
     */
    public TransactionListPage filterByCategory(String category) {
        page.locator(FILTER_CATEGORY).selectOption(category);
        page.waitForLoadState();
        return this;
    }

    /**
     * Click new transaction button.
     */
    public TransactionListPage clickNewTransaction() {
        page.locator(BTN_TRANSAKSI_BARU).click();
        return this;
    }

    /**
     * Verify transaction table is visible.
     */
    public TransactionListPage verifyTableVisible() {
        assertThat(page.locator(TRANSACTION_TABLE)).isVisible();
        return this;
    }

    /**
     * Take screenshot.
     */
    public TransactionListPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
