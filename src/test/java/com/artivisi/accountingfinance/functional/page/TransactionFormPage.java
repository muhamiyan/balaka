package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
     * Drive the accountPicker combobox for a template-line account mapping. The
     * picker input id is accountMapping_<lineId>; we type the accountId (treated
     * as a code substring) and click the first result. Callers that pass a real
     * UUID won't match — server search returns accounts by code/name, not id.
     */
    public TransactionFormPage selectAccountMapping(String mappingId, String search) {
        var input = page.locator("#" + mappingId);
        input.click();
        input.fill(search);
        page.waitForTimeout(400);
        var results = page.locator("[data-testid='account-picker-result']");
        if (results.count() > 0) {
            results.first().click();
        }
        return this;
    }

    /**
     * Fill inputs from a pipe-separated string.
     * Format: "field1:value1|field2:value2|..."
     * Special handling for account hints: "BANK:1.1.01" will select account 1.1.01 in the BANK dropdown
     */
    public TransactionFormPage fillInputs(String inputs) {
        Map<String, String> inputMap = parseInputs(inputs);
        for (var entry : inputMap.entrySet()) {
            String fieldId = entry.getKey();
            String value = entry.getValue();

            if (fieldId.startsWith("accountMapping")) {
                // Combobox: type the value (account code) and pick the first match.
                var input = page.locator("#" + fieldId);
                input.click();
                input.fill(value);
                page.waitForTimeout(400);
                var results = page.locator("[data-testid='account-picker-result']");
                if (results.count() > 0) {
                    results.first().click();
                }
            } else if (fieldId.equals("amount")) {
                fillAmount(value);
            } else if (fieldId.startsWith("var_")) {
                page.locator("#" + fieldId).fill(value);
            } else if (isAccountHint(fieldId)) {
                // Handle account hints like "BANK:1.1.01"
                selectAccountByHint(fieldId, value);
            }
        }
        return this;
    }

    /**
     * Check if field is a known account hint.
     */
    private boolean isAccountHint(String fieldId) {
        return fieldId.equals("BANK") || fieldId.equals("PENDAPATAN") ||
               fieldId.equals("BEBAN") || fieldId.equals("PIUTANG") ||
               fieldId.equals("HUTANG");
    }

    /**
     * Select account by hint and account code.
     * Finds the select element whose label contains the hint text.
     */
    private void selectAccountByHint(String hint, String accountCode) {
        // Each template-line picker is an input id=accountMapping_<lineId>. Find
        // the one whose label contains the hint and drive its combobox.
        var inputs = page.locator("input[id^='accountMapping_']").all();
        for (var input : inputs) {
            String inputId = input.getAttribute("id");
            var label = page.locator("label[for='" + inputId + "']");
            String labelText = label.textContent();
            if (labelText == null || !labelText.contains(hint)) continue;

            input.click();
            input.fill(accountCode);
            page.waitForTimeout(400);
            var results = page.locator("[data-testid='account-picker-result']");
            int n = Math.min(results.count(), 10);
            for (int i = 0; i < n; i++) {
                String code = results.nth(i).locator(".font-medium").textContent();
                if (code != null && code.startsWith(accountCode)) {
                    results.nth(i).click();
                    return;
                }
            }
            if (n > 0) {
                results.first().click();
            }
            return;
        }
    }

    /**
     * Click save and post button.
     * Returns TransactionDetailPage after redirect.
     */
    public TransactionDetailPage saveAndPost() {
        // Wait for Alpine.js to initialize and button to be enabled
        page.waitForTimeout(500);
        var btn = page.locator(BTN_SAVE_POST);
        btn.waitFor();
        btn.click();
        // Wait for redirect to detail page (UUID pattern, not /new)
        page.waitForURL(java.util.regex.Pattern.compile(".*/transactions/[0-9a-f]{8}-[0-9a-f]{4}-.*"));
        page.waitForLoadState();
        return new TransactionDetailPage(page, baseUrl);
    }

    /**
     * Click save as draft button.
     * Returns TransactionDetailPage after redirect.
     */
    public TransactionDetailPage saveAsDraft() {
        page.locator(BTN_SAVE_DRAFT).click();
        // Wait for redirect to detail page (UUID pattern, not /new)
        page.waitForURL(java.util.regex.Pattern.compile(".*/transactions/[0-9a-f]{8}-[0-9a-f]{4}-.*"));
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

    /**
     * Wait for preview to load after amount change.
     * HTMX loads the preview asynchronously.
     */
    public TransactionFormPage waitForPreviewUpdate() {
        // Wait for HTMX to finish loading
        page.waitForTimeout(1000);
        page.locator("#total-debit").waitFor();
        return this;
    }

    /**
     * Get the total debit amount from preview.
     * Returns the numeric value parsed from "Rp X.XXX.XXX" format.
     */
    public long getPreviewTotalDebit() {
        String text = page.locator("#total-debit").textContent();
        return parseRupiahAmount(text);
    }

    /**
     * Get the total credit amount from preview.
     * Returns the numeric value parsed from "Rp X.XXX.XXX" format.
     */
    public long getPreviewTotalCredit() {
        String text = page.locator("#total-credit").textContent();
        return parseRupiahAmount(text);
    }

    /**
     * Parse Rupiah formatted amount to long.
     * "Rp 3.333.333" -> 3333333
     */
    private long parseRupiahAmount(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // Remove "Rp " prefix and thousand separators
        String cleaned = text.replace("Rp", "").replace(".", "").trim();
        try {
            return Long.parseLong(cleaned);
        } catch (NumberFormatException _) {
            return 0;
        }
    }
}
