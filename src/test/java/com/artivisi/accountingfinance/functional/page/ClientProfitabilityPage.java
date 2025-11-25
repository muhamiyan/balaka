package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ClientProfitabilityPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String CLIENT_SELECT = "#clientId";
    private static final String START_DATE = "#startDate";
    private static final String END_DATE = "#endDate";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String NO_CLIENT_SELECTED = "#no-client-selected";
    private static final String CLIENT_SUMMARY = "#client-summary";
    private static final String CLIENT_NAME = "#client-name";
    private static final String CLIENT_CODE = "#client-code";
    private static final String OVERALL_MARGIN = "#overall-margin";
    private static final String SUMMARY_CARDS = "#summary-cards";
    private static final String TOTAL_REVENUE = "#total-revenue";
    private static final String TOTAL_PROFIT = "#total-profit";
    private static final String PROJECT_COUNT = "#project-count";
    private static final String PROJECTS_TABLE = "#projects-table";
    private static final String PROJECT_ROW = ".project-row";

    public ClientProfitabilityPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public ClientProfitabilityPage navigate() {
        page.navigate(baseUrl + "/reports/client-profitability",
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    public ClientProfitabilityPage navigateWithClient(String clientId, String startDate, String endDate) {
        page.navigate(baseUrl + "/reports/client-profitability?clientId=" + clientId
            + "&startDate=" + startDate + "&endDate=" + endDate,
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    // Actions
    public void selectClient(String clientName) {
        page.selectOption(CLIENT_SELECT, new com.microsoft.playwright.options.SelectOption().setLabel(clientName));
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

    public void assertNoClientSelectedVisible() {
        assertThat(page.locator(NO_CLIENT_SELECTED)).isVisible();
    }

    public void assertClientSummaryVisible() {
        assertThat(page.locator(CLIENT_SUMMARY)).isVisible();
    }

    public void assertSummaryCardsVisible() {
        assertThat(page.locator(SUMMARY_CARDS)).isVisible();
    }

    public void assertProjectsTableVisible() {
        assertThat(page.locator(PROJECTS_TABLE)).isVisible();
    }

    public String getClientNameText() {
        return page.locator(CLIENT_NAME).textContent();
    }

    public String getClientCodeText() {
        return page.locator(CLIENT_CODE).textContent();
    }

    public String getOverallMarginText() {
        return page.locator(OVERALL_MARGIN).textContent();
    }

    public String getTotalRevenueText() {
        return page.locator(TOTAL_REVENUE).textContent();
    }

    public String getTotalProfitText() {
        return page.locator(TOTAL_PROFIT).textContent();
    }

    public String getProjectCountText() {
        return page.locator(PROJECT_COUNT).textContent();
    }

    public int getProjectRowCount() {
        return page.locator(PROJECT_ROW).count();
    }

    public void assertClientSelectVisible() {
        assertThat(page.locator(CLIENT_SELECT)).isVisible();
    }

    public void assertGenerateButtonVisible() {
        assertThat(page.locator(BTN_GENERATE)).isVisible();
    }
}
