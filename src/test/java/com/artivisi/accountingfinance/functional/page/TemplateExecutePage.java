package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class TemplateExecutePage {
    private final Page page;
    private final String baseUrl;

    // Locators - Updated to match consolidated transaction form
    private static final String PAGE_TITLE = "#page-title";
    private static final String FORM_CONTENT = "#transaction-form-content";
    private static final String TRANSACTION_DATE = "#transactionDate";
    private static final String AMOUNT = "#amount";
    private static final String DESCRIPTION = "#description";
    private static final String PREVIEW_CONTENT = "#preview-content";
    private static final String SAVE_POST_BUTTON = "#btn-simpan-posting";
    private static final String TRANSACTION_DETAIL_CONTENT = "#transaction-detail-content";
    private static final String ERROR_MESSAGE = ".bg-red-50, .text-red-600";

    public TemplateExecutePage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public TemplateExecutePage navigate(String templateId) {
        // Navigate to consolidated transaction form with template
        page.navigate(baseUrl + "/transactions/new?templateId=" + templateId);
        page.waitForLoadState();
        // Wait for Alpine.js to initialize
        page.waitForSelector("[x-data]");
        return this;
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertPageTitleText(String expectedText) {
        assertThat(page.locator(PAGE_TITLE)).containsText(expectedText);
    }

    public void assertTemplateNameVisible() {
        // Template name is shown in the info card, look for "Template:" text
        assertThat(page.locator("text=Template:")).isVisible();
    }

    public void assertTemplateNameText(String expectedText) {
        // Template name is shown in the info card
        assertThat(page.locator("text=" + expectedText).first()).isVisible();
    }

    public void assertTransactionDateVisible() {
        assertThat(page.locator(TRANSACTION_DATE)).isVisible();
    }

    public void assertAmountInputVisible() {
        assertThat(page.locator(AMOUNT)).isVisible();
    }

    public void assertDescriptionInputVisible() {
        assertThat(page.locator(DESCRIPTION)).isVisible();
    }

    public void assertPreviewButtonVisible() {
        // In consolidated form, there's no preview button - preview is loaded via HTMX automatically
        // Check that the preview section exists instead
        assertThat(page.locator("#journal-preview")).isVisible();
    }

    public void fillTransactionDate(String date) {
        page.fill(TRANSACTION_DATE, date);
    }

    public void fillAmount(String amount) {
        // Fill the visible input field
        page.fill(AMOUNT, amount);

        // Update Alpine.js model and hidden input via JavaScript, then trigger HTMX
        String jsCode = String.format("""
            (() => {
                const amountValue = parseInt('%s'.replace(/\\D/g, '')) || 0;

                // Update Alpine.js model
                const amountInput = document.querySelector('#amount');
                const form = amountInput.closest('[x-data]');
                if (form && form.__x && form.__x.$data) {
                    form.__x.$data.amount = amountValue;
                } else if (form && form._x_dataStack && form._x_dataStack[0]) {
                    form._x_dataStack[0].amount = amountValue;
                }

                // Update the hidden amount input
                const hiddenAmount = document.querySelector('input[name="amount"][type="hidden"]');
                if (hiddenAmount) {
                    hiddenAmount.value = amountValue;
                }

                // Trigger HTMX on the preview content element
                const previewContent = document.querySelector('#preview-content');
                if (previewContent && typeof htmx !== 'undefined') {
                    htmx.trigger(previewContent, 'amount-changed');
                } else {
                    // Fallback: dispatch custom event
                    document.body.dispatchEvent(new CustomEvent('amount-changed', { bubbles: true }));
                }
            })()
            """, amount);
        page.evaluate(jsCode);
    }

    public void fillDescription(String description) {
        page.fill(DESCRIPTION, description);
    }

    public void clickPreviewButton() {
        // In consolidated form, preview is triggered automatically via HTMX on amount/account change
        // Wait for preview rows to appear
        page.waitForSelector(PREVIEW_CONTENT + " .preview-row");
    }

    public void clickExecuteButton() {
        // In consolidated form, use "Simpan & Posting" button
        page.click(SAVE_POST_BUTTON);
        // Wait for redirect to transaction detail page
        page.waitForSelector(TRANSACTION_DETAIL_CONTENT,
            new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(30000));
    }

    public void assertPreviewTableVisible() {
        // Wait for preview rows to be loaded via HTMX
        page.waitForSelector(PREVIEW_CONTENT + " .preview-row");
        assertThat(page.locator(PREVIEW_CONTENT)).isVisible();
    }

    public void assertTotalDebitText(String expectedText) {
        // Total is in the preview content section
        assertThat(page.locator(PREVIEW_CONTENT).getByText("Rp " + expectedText).first()).isVisible();
    }

    public void assertTotalCreditText(String expectedText) {
        // Total is in the preview content section
        assertThat(page.locator(PREVIEW_CONTENT).getByText("Rp " + expectedText).last()).isVisible();
    }

    public void assertBalanceStatusVisible() {
        // Balance status shows "Jurnal Balance" text
        assertThat(page.locator("text=Jurnal Balance")).isVisible();
    }

    public void assertBalanced() {
        assertThat(page.locator("text=Jurnal Balance")).isVisible();
    }

    public void assertRedirectedToTransactionDetail() {
        // Wait for redirect to complete
        page.waitForURL("**/transactions/**");
        // Verify we're on transaction detail page by checking for detail content
        assertThat(page.locator(TRANSACTION_DETAIL_CONTENT)).isVisible();
    }

    public void assertSuccessMessageVisible() {
        // In consolidated form, success is indicated by being on the detail page
        // Check for "Transaksi" in page title or similar indicator
        assertThat(page.locator(TRANSACTION_DETAIL_CONTENT)).isVisible();
    }

    public void assertErrorMessageVisible() {
        // Look for error message elements or validation messages
        assertThat(page.locator(ERROR_MESSAGE).first()).isVisible();
    }

    public void assertErrorMessageText(String expectedText) {
        assertThat(page.locator("text=" + expectedText)).isVisible();
    }

    public int getPreviewRowCount() {
        // Wait for preview rows to be loaded via HTMX
        page.waitForSelector(PREVIEW_CONTENT + " .preview-row");
        return page.locator(PREVIEW_CONTENT + " .preview-row").count();
    }

    public void assertAccountCodeVisible(int rowIndex) {
        // Preview rows have data-row-index attribute
        String selector = PREVIEW_CONTENT + " [data-row-index='" + rowIndex + "'] .preview-account-code";
        assertThat(page.locator(selector)).isVisible();
    }

    public void assertAccountNameVisible(int rowIndex) {
        // Preview rows have data-row-index attribute
        String selector = PREVIEW_CONTENT + " [data-row-index='" + rowIndex + "'] .preview-account-name";
        assertThat(page.locator(selector)).isVisible();
    }

    public String getAccountCode(int rowIndex) {
        String selector = PREVIEW_CONTENT + " [data-row-index='" + rowIndex + "'] .preview-account-code";
        return page.locator(selector).textContent().trim();
    }

    public String getAccountName(int rowIndex) {
        String selector = PREVIEW_CONTENT + " [data-row-index='" + rowIndex + "'] .preview-account-name";
        return page.locator(selector).textContent().trim();
    }

    public void assertAccountCodeNotEmpty(int rowIndex) {
        String accountCode = getAccountCode(rowIndex);
        assertThat(accountCode).isNotEmpty();
    }

    public void assertAccountNameNotEmpty(int rowIndex) {
        String accountName = getAccountName(rowIndex);
        assertThat(accountName).isNotEmpty();
    }
}
