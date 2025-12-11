package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import java.util.UUID;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Journal Ledger / General Ledger (/journals).
 */
public class JournalLedgerPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs only
    private static final String PAGE_TITLE = "#page-title";
    private static final String FILTER_FORM = "#filter-form";
    private static final String ACCOUNT_FILTER = "#account-filter";
    private static final String START_DATE = "#start-date";
    private static final String END_DATE = "#end-date";
    private static final String SEARCH_INPUT = "#search-input";
    private static final String BTN_APPLY = "#btn-apply";
    private static final String BTN_ADD_JOURNAL = "#btn-add-journal";
    private static final String JOURNAL_LEDGER = "#journal-ledger";
    private static final String ACCOUNT_NAME = "#account-name";
    private static final String ENTRIES_CONTENT = "#entries-content";
    private static final String OPENING_BALANCE_ROW = "#opening-balance-row";
    private static final String CLOSING_BALANCE = "#closing-balance";

    public JournalLedgerPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public JournalLedgerPage navigate() {
        page.navigate(baseUrl + "/journals");
        page.waitForLoadState();
        return this;
    }

    public JournalLedgerPage navigate(UUID accountId, String startDate, String endDate) {
        page.navigate(baseUrl + "/journals?accountId=" + accountId + "&startDate=" + startDate + "&endDate=" + endDate);
        page.waitForLoadState();
        return this;
    }

    public JournalLedgerPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Buku Besar");
        return this;
    }

    public JournalLedgerPage selectAccount(UUID accountId) {
        page.locator(ACCOUNT_FILTER).selectOption(accountId.toString());
        return this;
    }

    public JournalLedgerPage setDateRange(String startDate, String endDate) {
        page.locator(START_DATE).fill(startDate);
        page.locator(END_DATE).fill(endDate);
        return this;
    }

    public JournalLedgerPage search(String query) {
        page.locator(SEARCH_INPUT).fill(query);
        return this;
    }

    public JournalLedgerPage apply() {
        page.locator(BTN_APPLY).click();
        page.waitForLoadState();
        return this;
    }

    public JournalLedgerPage verifyAccountNameVisible(String accountCode, String accountName) {
        assertThat(page.locator(ACCOUNT_NAME)).containsText(accountCode);
        assertThat(page.locator(ACCOUNT_NAME)).containsText(accountName);
        return this;
    }

    public JournalLedgerPage verifyEntriesContentVisible() {
        assertThat(page.locator(ENTRIES_CONTENT)).isVisible();
        return this;
    }

    public JournalLedgerPage verifyOpeningBalanceVisible() {
        assertThat(page.locator(OPENING_BALANCE_ROW)).isVisible();
        return this;
    }

    public JournalLedgerPage verifyClosingBalance(String amount) {
        assertThat(page.locator(CLOSING_BALANCE)).containsText(amount);
        return this;
    }

    public JournalLedgerPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
