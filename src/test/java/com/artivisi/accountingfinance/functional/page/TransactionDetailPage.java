package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Transaction Detail (/transactions/{id}).
 * Handles viewing and verifying transaction details.
 */
public class TransactionDetailPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs and data-testid attributes
    private static final String PAGE_TITLE = "#page-title";
    private static final String TRANSACTION_NUMBER = "#transaction-number";
    private static final String TRANSACTION_STATUS = "#transaction-status";
    private static final String JOURNAL_ENTRIES = "#journal-entries";
    private static final String JOURNAL_ENTRY_ROW = ".journal-entry-row";
    private static final String JOURNAL_TOTALS = "#journal-totals";
    private static final String TOTAL_DEBIT = "#total-debit";
    private static final String TOTAL_CREDIT = "#total-credit";
    private static final String STATUS_POSTED = "[data-testid='status-posted']";
    private static final String STATUS_DRAFT = "[data-testid='status-draft']";
    private static final String STATUS_VOID = "[data-testid='status-void']";
    private static final String BTN_POST = "#btn-post";
    private static final String BTN_DELETE = "#btn-delete";

    public TransactionDetailPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    /**
     * Navigate to transaction detail by ID.
     */
    public TransactionDetailPage navigateTo(String transactionId) {
        page.navigate(baseUrl + "/transactions/" + transactionId);
        page.waitForLoadState();
        return this;
    }

    /**
     * Get page title text.
     */
    public String getPageTitle() {
        return page.locator(PAGE_TITLE).textContent();
    }

    /**
     * Get transaction number text.
     */
    public String getTransactionNumber() {
        return page.locator(TRANSACTION_NUMBER).textContent();
    }

    /**
     * Verify page title contains expected text.
     */
    public TransactionDetailPage verifyPageTitle(String expected) {
        assertThat(page.locator(PAGE_TITLE)).containsText(expected);
        return this;
    }

    /**
     * Verify transaction is POSTED.
     */
    public TransactionDetailPage verifyStatusPosted() {
        assertThat(page.locator(STATUS_POSTED)).isVisible();
        return this;
    }

    /**
     * Verify transaction is DRAFT.
     */
    public TransactionDetailPage verifyStatusDraft() {
        assertThat(page.locator(STATUS_DRAFT)).isVisible();
        return this;
    }

    /**
     * Verify transaction is VOID.
     */
    public TransactionDetailPage verifyStatusVoid() {
        assertThat(page.locator(STATUS_VOID)).isVisible();
        return this;
    }

    /**
     * Verify journal entries section is visible.
     */
    public TransactionDetailPage verifyJournalEntriesVisible() {
        assertThat(page.locator(JOURNAL_ENTRIES)).isVisible();
        return this;
    }

    /**
     * Click post button (for draft transactions).
     */
    public TransactionDetailPage clickPost() {
        page.locator(BTN_POST).click();
        page.waitForLoadState();
        return this;
    }

    /**
     * Verify journal entries count.
     */
    public TransactionDetailPage verifyJournalEntryCount(int expectedCount) {
        assertThat(page.locator(JOURNAL_ENTRY_ROW)).hasCount(expectedCount);
        return this;
    }

    /**
     * Verify journal entry at index has expected account code.
     * @param index 0-based index
     * @param expectedAccountCode e.g., "1.1.01"
     */
    public TransactionDetailPage verifyJournalEntryAccountCode(int index, String expectedAccountCode) {
        assertThat(page.locator("#entry-account-code-" + index)).containsText(expectedAccountCode);
        return this;
    }

    /**
     * Verify journal entry at index has expected account name.
     * @param index 0-based index
     * @param expectedAccountName e.g., "Kas"
     */
    public TransactionDetailPage verifyJournalEntryAccountName(int index, String expectedAccountName) {
        assertThat(page.locator("#entry-account-name-" + index)).containsText(expectedAccountName);
        return this;
    }

    /**
     * Verify journal entry at index has expected debit amount.
     * @param index 0-based index
     * @param expectedAmount formatted amount e.g., "500.000.000"
     */
    public TransactionDetailPage verifyJournalEntryDebit(int index, String expectedAmount) {
        assertThat(page.locator("#entry-debit-" + index)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify journal entry at index has expected credit amount.
     * @param index 0-based index
     * @param expectedAmount formatted amount e.g., "500.000.000"
     */
    public TransactionDetailPage verifyJournalEntryCredit(int index, String expectedAmount) {
        assertThat(page.locator("#entry-credit-" + index)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify total debit amount.
     * @param expectedAmount formatted amount e.g., "500.000.000"
     */
    public TransactionDetailPage verifyTotalDebit(String expectedAmount) {
        assertThat(page.locator(TOTAL_DEBIT)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify total credit amount.
     * @param expectedAmount formatted amount e.g., "500.000.000"
     */
    public TransactionDetailPage verifyTotalCredit(String expectedAmount) {
        assertThat(page.locator(TOTAL_CREDIT)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify debit equals credit (balanced journal).
     */
    public TransactionDetailPage verifyJournalBalanced() {
        String debit = page.locator(TOTAL_DEBIT).textContent();
        String credit = page.locator(TOTAL_CREDIT).textContent();
        if (!debit.equals(credit)) {
            throw new AssertionError("Journal not balanced: Debit=" + debit + ", Credit=" + credit);
        }
        return this;
    }

    /**
     * Take screenshot.
     */
    public TransactionDetailPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
