package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Tax Calendar (/tax-calendar).
 */
public class TaxCalendarPage {

    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";

    public TaxCalendarPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public TaxCalendarPage navigate() {
        page.navigate(baseUrl + "/tax-calendar");
        page.waitForLoadState();
        return this;
    }

    public TaxCalendarPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Kalender Pajak");
        return this;
    }

    public TaxCalendarPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
