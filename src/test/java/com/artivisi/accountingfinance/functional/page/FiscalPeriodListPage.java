package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class FiscalPeriodListPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String PERIOD_TABLE = "[data-testid='period-table']";
    private static final String NEW_PERIOD_BUTTON = "#btn-new-period";
    private static final String GENERATE_YEAR_BUTTON = "#btn-generate-year";

    public FiscalPeriodListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public FiscalPeriodListPage navigate() {
        page.navigate(baseUrl + "/fiscal-periods");
        return this;
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE).isVisible()).isTrue();
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void assertTableVisible() {
        assertThat(page.locator(PERIOD_TABLE).isVisible()).isTrue();
    }

    public void clickNewPeriodButton() {
        page.click(NEW_PERIOD_BUTTON);
        page.waitForLoadState();
    }

    public boolean hasPeriodWithName(String periodName) {
        return page.locator(PERIOD_TABLE).textContent().contains(periodName);
    }

    public void filterByYear(int year) {
        page.selectOption("[name='year']", String.valueOf(year));
        page.waitForLoadState();
    }

    public void filterByStatus(String status) {
        page.selectOption("[name='status']", status);
        page.waitForLoadState();
    }
}
