package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ClientRankingPage {
    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String START_DATE = "#startDate";
    private static final String END_DATE = "#endDate";
    private static final String LIMIT_SELECT = "#limit";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String NO_DATA_MESSAGE = "#no-data-message";
    private static final String RANKING_TABLE = "#ranking-table";
    private static final String RANKING_ROW = ".ranking-row";

    public ClientRankingPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public ClientRankingPage navigate() {
        page.navigate(baseUrl + "/reports/client-ranking",
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    public ClientRankingPage navigateWithParams(String startDate, String endDate, int limit) {
        page.navigate(baseUrl + "/reports/client-ranking?startDate=" + startDate
            + "&endDate=" + endDate + "&limit=" + limit,
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

    public void selectLimit(String limit) {
        page.selectOption(LIMIT_SELECT, limit);
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

    public void assertNoDataMessageVisible() {
        assertThat(page.locator(NO_DATA_MESSAGE)).isVisible();
    }

    public void assertNoDataMessageNotVisible() {
        assertThat(page.locator(NO_DATA_MESSAGE)).not().isVisible();
    }

    public void assertRankingTableVisible() {
        assertThat(page.locator(RANKING_TABLE)).isVisible();
    }

    public int getRankingRowCount() {
        return page.locator(RANKING_ROW).count();
    }

    public void assertGenerateButtonVisible() {
        assertThat(page.locator(BTN_GENERATE)).isVisible();
    }

    public void assertLimitSelectVisible() {
        assertThat(page.locator(LIMIT_SELECT)).isVisible();
    }

    public void assertStartDateVisible() {
        assertThat(page.locator(START_DATE)).isVisible();
    }

    public void assertEndDateVisible() {
        assertThat(page.locator(END_DATE)).isVisible();
    }
}
