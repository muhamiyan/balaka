package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Transaction Form (/transactions/new).
 * Handles creating new journal transactions via UI.
 */
public class TransactionFormPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs only
    private static final String TRANSACTION_DATE = "#transactionDate";
    private static final String AMOUNT = "#amount";
    private static final String DESCRIPTION = "#description";
    private static final String REFERENCE_NUMBER = "#referenceNumber";
    private static final String PROJECT = "#idProject";
    private static final String BTN_SAVE_POST = "#btn-simpan-posting";
    private static final String BTN_SAVE_DRAFT = "#btn-simpan-draft";

    public TransactionFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    /**
     * Navigate to new transaction form with specific template.
     */
    public TransactionFormPage navigateWithTemplate(UUID templateId) {
        page.navigate(baseUrl + "/transactions/new?templateId=" + templateId);
        page.waitForLoadState();
        return this;
    }

    /**
     * Fill transaction date.
     */
    public TransactionFormPage fillDate(String date) {
        page.locator(TRANSACTION_DATE).fill(date);
        return this;
    }

    /**
     * Fill amount for SIMPLE template.
     */
    public TransactionFormPage fillAmount(String amount) {
        page.locator(AMOUNT).fill(amount);
        return this;
    }

    /**
     * Fill description.
     */
    public TransactionFormPage fillDescription(String description) {
        page.locator(DESCRIPTION).fill(description);
        return this;
    }

    /**
     * Fill reference number.
     */
    public TransactionFormPage fillReferenceNumber(String reference) {
        if (reference != null && !reference.isEmpty()) {
            page.locator(REFERENCE_NUMBER).fill(reference);
        }
        return this;
    }

    /**
     * Select project by ID.
     */
    public TransactionFormPage selectProject(String projectId) {
        if (projectId != null && !projectId.isEmpty()) {
            page.locator(PROJECT).selectOption(new String[]{projectId});
        }
        return this;
    }

    /**
     * Fill a DETAILED template variable by ID.
     */
    public TransactionFormPage fillVariable(String variableId, String value) {
        page.locator("#" + variableId).fill(value);
        return this;
    }

    /**
     * Select dynamic account mapping by ID.
     */
    public TransactionFormPage selectAccountMapping(String mappingId, String accountId) {
        page.locator("#" + mappingId).selectOption(accountId);
        return this;
    }

    /**
     * Fill inputs from a pipe-separated string.
     * Format: "field1:value1|field2:value2|..."
     */
    public TransactionFormPage fillInputs(String inputs) {
        Map<String, String> inputMap = parseInputs(inputs);
        for (var entry : inputMap.entrySet()) {
            String fieldId = entry.getKey();
            String value = entry.getValue();

            if (fieldId.startsWith("accountMapping")) {
                page.locator("#" + fieldId).selectOption(value);
            } else if (fieldId.equals("amount")) {
                fillAmount(value);
            } else if (fieldId.startsWith("var_")) {
                page.locator("#" + fieldId).fill(value);
            }
        }
        return this;
    }

    /**
     * Click save and post button.
     * Returns TransactionDetailPage after redirect.
     */
    public TransactionDetailPage saveAndPost() {
        page.locator(BTN_SAVE_POST).click();
        page.waitForURL("**/transactions/*");
        page.waitForLoadState();
        return new TransactionDetailPage(page, baseUrl);
    }

    /**
     * Click save as draft button.
     * Returns TransactionDetailPage after redirect.
     */
    public TransactionDetailPage saveAsDraft() {
        page.locator(BTN_SAVE_DRAFT).click();
        page.waitForURL("**/transactions/*");
        page.waitForLoadState();
        return new TransactionDetailPage(page, baseUrl);
    }

    /**
     * Take screenshot of the form.
     */
    public TransactionFormPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }

    /**
     * Parse inputs string from CSV into key-value map.
     */
    private Map<String, String> parseInputs(String inputs) {
        Map<String, String> result = new HashMap<>();
        if (inputs == null || inputs.isEmpty()) {
            return result;
        }

        String[] pairs = inputs.split("\\|");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                result.put(kv[0].trim(), kv[1].trim());
            }
        }
        return result;
    }
}
