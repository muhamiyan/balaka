package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Trial Balance Report (/reports/trial-balance).
 */
public class TrialBalancePage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs only
    private static final String PAGE_TITLE = "#page-title";
    private static final String START_DATE = "#startDate";
    private static final String END_DATE = "#endDate";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String BTN_EXPORT_PDF = "#btn-export-pdf";
    private static final String BTN_EXPORT_EXCEL = "#btn-export-excel";

    public TrialBalancePage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public TrialBalancePage navigate() {
        page.navigate(baseUrl + "/reports/trial-balance");
        page.waitForLoadState();
        return this;
    }

    public TrialBalancePage navigate(String startDate, String endDate) {
        page.navigate(baseUrl + "/reports/trial-balance?startDate=" + startDate + "&endDate=" + endDate);
        page.waitForLoadState();
        return this;
    }

    public TrialBalancePage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Neraca Saldo");
        return this;
    }

    public TrialBalancePage generateReport(String startDate, String endDate) {
        page.locator(START_DATE).fill(startDate);
        page.locator(END_DATE).fill(endDate);
        page.locator(BTN_GENERATE).click();
        page.waitForLoadState();
        return this;
    }

    public TrialBalancePage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
