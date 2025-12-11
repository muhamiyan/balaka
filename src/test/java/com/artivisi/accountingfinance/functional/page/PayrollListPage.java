package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Payroll List (/payroll).
 */
public class PayrollListPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs only
    private static final String PAGE_TITLE = "#page-title";
    private static final String BTN_NEW_PAYROLL = "#btn-new-payroll";
    private static final String BTN_BUKTI_POTONG = "#btn-bukti-potong";
    private static final String STATUS_FILTER = "#status";

    public PayrollListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public PayrollListPage navigate() {
        page.navigate(baseUrl + "/payroll");
        page.waitForLoadState();
        return this;
    }

    public PayrollListPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Payroll");
        return this;
    }

    public PayrollListPage filterByStatus(String status) {
        page.locator(STATUS_FILTER).selectOption(status);
        page.waitForLoadState();
        return this;
    }

    public PayrollListPage clickNewPayroll() {
        page.locator(BTN_NEW_PAYROLL).click();
        page.waitForLoadState();
        return this;
    }

    public PayrollListPage clickBuktiPotong() {
        page.locator(BTN_BUKTI_POTONG).click();
        page.waitForLoadState();
        return this;
    }

    public PayrollListPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
