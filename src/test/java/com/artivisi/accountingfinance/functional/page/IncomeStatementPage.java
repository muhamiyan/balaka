package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Income Statement Report (/reports/income-statement).
 * Handles viewing and verifying income statement (P&L) report.
 */
public class IncomeStatementPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs only
    private static final String PAGE_TITLE = "#page-title";
    private static final String REPORT_TITLE = "#report-title";
    private static final String REPORT_PERIOD = "#report-period";
    private static final String START_DATE = "#startDate";
    private static final String END_DATE = "#endDate";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String BTN_EXPORT_PDF = "#btn-export-pdf";
    private static final String BTN_EXPORT_EXCEL = "#btn-export-excel";
    private static final String BTN_PRINT = "#btn-print";
    private static final String REVENUE_ITEMS = "#revenue-items";
    private static final String EXPENSE_ITEMS = "#expense-items";
    private static final String TOTAL_REVENUE = "#total-revenue";
    private static final String TOTAL_EXPENSE = "#total-expense";
    private static final String NET_INCOME = "#net-income";
    private static final String PROFIT_STATUS = "#profit-status";

    public IncomeStatementPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    /**
     * Navigate to income statement report.
     */
    public IncomeStatementPage navigate() {
        page.navigate(baseUrl + "/reports/income-statement");
        page.waitForLoadState();
        return this;
    }

    /**
     * Navigate to income statement report with date range.
     */
    public IncomeStatementPage navigate(String startDate, String endDate) {
        page.navigate(baseUrl + "/reports/income-statement?startDate=" + startDate + "&endDate=" + endDate);
        page.waitForLoadState();
        return this;
    }

    /**
     * Navigate with year and month parameters.
     */
    public IncomeStatementPage navigateWithYearMonth(int year, int month) {
        String startDate = String.format("%d-%02d-01", year, month);
        String endDate = String.format("%d-%02d-%02d", year, month, getLastDayOfMonth(year, month));
        return navigate(startDate, endDate);
    }

    /**
     * Fill date range and generate report.
     */
    public IncomeStatementPage generateReport(String startDate, String endDate) {
        page.locator(START_DATE).fill(startDate);
        page.locator(END_DATE).fill(endDate);
        page.locator(BTN_GENERATE).click();
        page.waitForLoadState();
        return this;
    }

    /**
     * Verify page title.
     */
    public IncomeStatementPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Laba Rugi");
        return this;
    }

    /**
     * Verify report title.
     */
    public IncomeStatementPage verifyReportTitle() {
        assertThat(page.locator(REPORT_TITLE)).containsText("LAPORAN LABA RUGI");
        return this;
    }

    /**
     * Verify revenue section is visible.
     */
    public IncomeStatementPage verifyRevenueSectionVisible() {
        assertThat(page.locator(REVENUE_ITEMS)).isVisible();
        return this;
    }

    /**
     * Verify expense section is visible.
     */
    public IncomeStatementPage verifyExpenseSectionVisible() {
        assertThat(page.locator(EXPENSE_ITEMS)).isVisible();
        return this;
    }

    /**
     * Verify net income is visible.
     */
    public IncomeStatementPage verifyNetIncomeVisible() {
        assertThat(page.locator(NET_INCOME)).isVisible();
        return this;
    }

    /**
     * Get total revenue text.
     */
    public String getTotalRevenue() {
        return page.locator(TOTAL_REVENUE).textContent();
    }

    /**
     * Get total expense text.
     */
    public String getTotalExpense() {
        return page.locator(TOTAL_EXPENSE).textContent();
    }

    /**
     * Get net income text.
     */
    public String getNetIncome() {
        return page.locator(NET_INCOME).textContent();
    }

    /**
     * Verify total revenue amount.
     */
    public IncomeStatementPage verifyTotalRevenue(String expectedAmount) {
        assertThat(page.locator(TOTAL_REVENUE)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify total expense amount.
     */
    public IncomeStatementPage verifyTotalExpense(String expectedAmount) {
        assertThat(page.locator(TOTAL_EXPENSE)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify net income amount.
     */
    public IncomeStatementPage verifyNetIncome(String expectedAmount) {
        assertThat(page.locator(NET_INCOME)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify profit status shows profit (laba).
     */
    public IncomeStatementPage verifyProfitStatus() {
        assertThat(page.locator(PROFIT_STATUS)).containsText("laba");
        return this;
    }

    /**
     * Take screenshot.
     */
    public IncomeStatementPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }

    private int getLastDayOfMonth(int year, int month) {
        return java.time.YearMonth.of(year, month).lengthOfMonth();
    }
}
