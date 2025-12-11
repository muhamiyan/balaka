package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Chart of Accounts (/accounts).
 */
public class ChartOfAccountsPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs only
    private static final String PAGE_TITLE = "#page-title";
    private static final String ACCOUNTS_LIST_CONTENT = "#accounts-list-content";
    private static final String ACCOUNTS_TABLE = "#accounts-table";
    private static final String BTN_TAMBAH_AKUN = "#btn-tambah-akun";
    private static final String SUCCESS_MESSAGE = "#success-message";
    private static final String ERROR_MESSAGE = "#error-message";

    public ChartOfAccountsPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public ChartOfAccountsPage navigate() {
        page.navigate(baseUrl + "/accounts");
        page.waitForLoadState();
        return this;
    }

    public ChartOfAccountsPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Bagan Akun");
        return this;
    }

    public ChartOfAccountsPage verifyContentVisible() {
        assertThat(page.locator(ACCOUNTS_LIST_CONTENT)).isVisible();
        return this;
    }

    public ChartOfAccountsPage verifyTableVisible() {
        assertThat(page.locator(ACCOUNTS_TABLE)).isVisible();
        return this;
    }

    public ChartOfAccountsPage clickAddAccount() {
        page.locator(BTN_TAMBAH_AKUN).click();
        page.waitForLoadState();
        return this;
    }

    /**
     * Verify minimum number of accounts exist.
     * Uses account-row-* ID pattern from fragments.html
     */
    public ChartOfAccountsPage verifyMinimumAccountCount(int minCount) {
        int count = page.locator("[id^='account-row-']").count();
        if (count < minCount) {
            throw new AssertionError("Expected at least " + minCount + " accounts, but found " + count);
        }
        return this;
    }

    /**
     * Verify specific account exists by code (e.g., "1.1.01" -> "account-row-1-1-01").
     * Note: Child accounts may be hidden (collapsed tree), so we check count > 0 instead of isVisible.
     */
    public ChartOfAccountsPage verifyAccountExists(String accountCode) {
        String idSelector = "#account-row-" + accountCode.replace(".", "-");
        int count = page.locator(idSelector).count();
        if (count == 0) {
            throw new AssertionError("Account not found: " + accountCode + " (selector: " + idSelector + ")");
        }
        return this;
    }

    public ChartOfAccountsPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
