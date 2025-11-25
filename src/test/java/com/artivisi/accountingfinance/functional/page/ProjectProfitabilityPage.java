package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ProjectProfitabilityPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String PROJECT_SELECT = "#projectId";
    private static final String START_DATE = "#startDate";
    private static final String END_DATE = "#endDate";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String NO_PROJECT_SELECTED = "#no-project-selected";
    private static final String PROFITABILITY_REPORT = "#profitability-report";
    private static final String REPORT_TITLE = "#report-title";
    private static final String TOTAL_REVENUE = "#total-revenue";
    private static final String TOTAL_EXPENSE = "#total-expense";
    private static final String PROFIT_SUMMARY = "#profit-summary";
    private static final String PROFIT_LABEL = "#profit-label";
    private static final String PROFIT_MARGIN = "#profit-margin";
    private static final String GROSS_PROFIT = "#gross-profit";
    private static final String COST_STATUS = "#cost-status";
    private static final String COST_OVERRUN_ALERT = "#cost-overrun-alert";
    private static final String RISK_LEVEL = "#risk-level";
    private static final String COST_BUDGET = "#cost-budget";
    private static final String COST_SPENT = "#cost-spent";

    public ProjectProfitabilityPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public ProjectProfitabilityPage navigate() {
        page.navigate(baseUrl + "/reports/project-profitability",
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    public ProjectProfitabilityPage navigateWithProject(String projectId, String startDate, String endDate) {
        page.navigate(baseUrl + "/reports/project-profitability?projectId=" + projectId
            + "&startDate=" + startDate + "&endDate=" + endDate,
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    // Actions
    public void selectProject(String projectName) {
        page.selectOption(PROJECT_SELECT, new com.microsoft.playwright.options.SelectOption().setLabel(projectName));
    }

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

    // Assertions
    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertPageTitleText(String expectedText) {
        assertThat(page.locator(PAGE_TITLE)).hasText(expectedText);
    }

    public void assertNoProjectSelectedVisible() {
        assertThat(page.locator(NO_PROJECT_SELECTED)).isVisible();
    }

    public void assertProfitabilityReportVisible() {
        assertThat(page.locator(PROFITABILITY_REPORT)).isVisible();
    }

    public void assertReportTitleVisible() {
        assertThat(page.locator(REPORT_TITLE)).isVisible();
    }

    public void assertReportTitleText(String expectedText) {
        assertThat(page.locator(REPORT_TITLE)).hasText(expectedText);
    }

    public void assertTotalRevenueVisible() {
        assertThat(page.locator(TOTAL_REVENUE)).isVisible();
    }

    public void assertTotalExpenseVisible() {
        assertThat(page.locator(TOTAL_EXPENSE)).isVisible();
    }

    public void assertProfitSummaryVisible() {
        assertThat(page.locator(PROFIT_SUMMARY)).isVisible();
    }

    public String getTotalRevenueText() {
        return page.locator(TOTAL_REVENUE).textContent();
    }

    public String getTotalExpenseText() {
        return page.locator(TOTAL_EXPENSE).textContent();
    }

    public String getGrossProfitText() {
        return page.locator(GROSS_PROFIT).textContent();
    }

    public String getProfitMarginText() {
        return page.locator(PROFIT_MARGIN).textContent();
    }

    public String getProfitLabelText() {
        return page.locator(PROFIT_LABEL).textContent();
    }

    public void assertCostStatusVisible() {
        assertThat(page.locator(COST_STATUS)).isVisible();
    }

    public void assertCostOverrunAlertVisible() {
        assertThat(page.locator(COST_OVERRUN_ALERT)).isVisible();
    }

    public void assertCostOverrunAlertNotVisible() {
        assertThat(page.locator(COST_OVERRUN_ALERT)).not().isVisible();
    }

    public String getRiskLevelText() {
        return page.locator(RISK_LEVEL).textContent();
    }

    public String getCostBudgetText() {
        return page.locator(COST_BUDGET).textContent();
    }

    public String getCostSpentText() {
        return page.locator(COST_SPENT).textContent();
    }

    public void assertProjectSelectVisible() {
        assertThat(page.locator(PROJECT_SELECT)).isVisible();
    }

    public void assertGenerateButtonVisible() {
        assertThat(page.locator(BTN_GENERATE)).isVisible();
    }
}
