package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class IncomeStatementPage {

    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String REPORT_TITLE = "#report-title";
    private static final String REPORT_PERIOD = "#report-period";
    private static final String START_DATE = "#startDate";
    private static final String END_DATE = "#endDate";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String BTN_PRINT = "#btn-print";
    private static final String TOTAL_REVENUE = "#total-revenue";
    private static final String TOTAL_EXPENSE = "#total-expense";
    private static final String NET_INCOME = "#net-income";
    private static final String PROFIT_STATUS = "#profit-status";
    private static final String PROFIT_MESSAGE = "#profit-message";
    private static final String REVENUE_ITEMS = "#revenue-items";
    private static final String EXPENSE_ITEMS = "#expense-items";
    private static final String REVENUE_ROW = ".revenue-row";
    private static final String EXPENSE_ROW = ".expense-row";

    public IncomeStatementPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public IncomeStatementPage navigate() {
        page.navigate(baseUrl + "/reports/income-statement",
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    public IncomeStatementPage navigateWithDates(String startDate, String endDate) {
        page.navigate(baseUrl + "/reports/income-statement?startDate=" + startDate + "&endDate=" + endDate,
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    // Actions
    public void setStartDate(String date) {
        page.fill(START_DATE, date);
    }

    public void setEndDate(String date) {
        page.fill(END_DATE, date);
    }

    public void clickGenerate() {
        page.click(BTN_GENERATE);
        page.waitForLoadState();
    }

    // Assertions - Page Elements
    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE)).hasText(expected);
    }

    public void assertReportTitleVisible() {
        assertThat(page.locator(REPORT_TITLE)).isVisible();
    }

    public void assertReportTitleText(String expected) {
        assertThat(page.locator(REPORT_TITLE)).hasText(expected);
    }

    public void assertStartDateVisible() {
        assertThat(page.locator(START_DATE)).isVisible();
    }

    public void assertEndDateVisible() {
        assertThat(page.locator(END_DATE)).isVisible();
    }

    public void assertGenerateButtonVisible() {
        assertThat(page.locator(BTN_GENERATE)).isVisible();
    }

    public void assertPrintButtonVisible() {
        assertThat(page.locator(BTN_PRINT)).isVisible();
    }

    public void assertReportPeriodContains(String expected) {
        assertThat(page.locator(REPORT_PERIOD)).containsText(expected);
    }

    // Assertions - Revenue Section
    public void assertRevenueItemsVisible() {
        assertThat(page.locator(REVENUE_ITEMS)).isVisible();
    }

    public void assertTotalRevenueVisible() {
        assertThat(page.locator(TOTAL_REVENUE)).isVisible();
    }

    public String getTotalRevenueText() {
        return page.locator(TOTAL_REVENUE).textContent().trim();
    }

    public int getRevenueRowCount() {
        return page.locator(REVENUE_ROW).count();
    }

    // Assertions - Expense Section
    public void assertExpenseItemsVisible() {
        assertThat(page.locator(EXPENSE_ITEMS)).isVisible();
    }

    public void assertTotalExpenseVisible() {
        assertThat(page.locator(TOTAL_EXPENSE)).isVisible();
    }

    public String getTotalExpenseText() {
        return page.locator(TOTAL_EXPENSE).textContent().trim();
    }

    public int getExpenseRowCount() {
        return page.locator(EXPENSE_ROW).count();
    }

    // Assertions - Net Income
    public void assertNetIncomeVisible() {
        assertThat(page.locator(NET_INCOME)).isVisible();
    }

    public String getNetIncomeText() {
        return page.locator(NET_INCOME).textContent().trim();
    }

    // Assertions - Profit Status
    public void assertProfitStatusVisible() {
        assertThat(page.locator(PROFIT_STATUS)).isVisible();
    }

    public void assertProfitMessageContains(String expected) {
        assertThat(page.locator(PROFIT_MESSAGE)).containsText(expected);
    }

    // Assertions - Account Names
    public void assertAccountNameExists(String accountName) {
        assertThat(page.locator(".account-name:has-text('" + accountName + "')")).isVisible();
    }
}
