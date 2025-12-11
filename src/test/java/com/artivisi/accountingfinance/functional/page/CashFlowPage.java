package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Cash Flow Report (/reports/cash-flow).
 */
public class CashFlowPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs only
    private static final String PAGE_TITLE = "#page-title";
    private static final String CASH_FLOW_CONTENT = "#cash-flow-content";
    private static final String REPORT_TITLE = "#report-title";
    private static final String START_DATE = "#startDate";
    private static final String END_DATE = "#endDate";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String OPERATING_TOTAL = "#operating-total";
    private static final String INVESTING_TOTAL = "#investing-total";
    private static final String FINANCING_TOTAL = "#financing-total";
    private static final String NET_CASH_CHANGE = "#net-cash-change";
    private static final String ENDING_BALANCE = "#ending-balance";

    public CashFlowPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public CashFlowPage navigate() {
        page.navigate(baseUrl + "/reports/cash-flow");
        page.waitForLoadState();
        return this;
    }

    public CashFlowPage navigate(String startDate, String endDate) {
        page.navigate(baseUrl + "/reports/cash-flow?startDate=" + startDate + "&endDate=" + endDate);
        page.waitForLoadState();
        return this;
    }

    public CashFlowPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Arus Kas");
        return this;
    }

    public CashFlowPage verifyContentVisible() {
        assertThat(page.locator(CASH_FLOW_CONTENT)).isVisible();
        return this;
    }

    public CashFlowPage generateReport(String startDate, String endDate) {
        page.locator(START_DATE).fill(startDate);
        page.locator(END_DATE).fill(endDate);
        page.locator(BTN_GENERATE).click();
        page.waitForLoadState();
        return this;
    }

    /**
     * Verify operating activities total.
     */
    public CashFlowPage verifyOperatingTotal(String expectedAmount) {
        assertThat(page.locator(OPERATING_TOTAL)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify investing activities total.
     */
    public CashFlowPage verifyInvestingTotal(String expectedAmount) {
        assertThat(page.locator(INVESTING_TOTAL)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify financing activities total.
     */
    public CashFlowPage verifyFinancingTotal(String expectedAmount) {
        assertThat(page.locator(FINANCING_TOTAL)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify net cash change.
     */
    public CashFlowPage verifyNetCashChange(String expectedAmount) {
        assertThat(page.locator(NET_CASH_CHANGE)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify ending cash balance.
     */
    public CashFlowPage verifyEndingBalance(String expectedAmount) {
        assertThat(page.locator(ENDING_BALANCE)).containsText(expectedAmount);
        return this;
    }

    public CashFlowPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
