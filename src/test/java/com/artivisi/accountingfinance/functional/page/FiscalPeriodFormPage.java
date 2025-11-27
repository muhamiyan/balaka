package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class FiscalPeriodFormPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String YEAR_INPUT = "#year";
    private static final String MONTH_SELECT = "#month";
    private static final String SUBMIT_BUTTON = "#btn-simpan";

    public FiscalPeriodFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public FiscalPeriodFormPage navigateToNew() {
        page.navigate(baseUrl + "/fiscal-periods/new");
        return this;
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE).textContent()).contains(expected);
    }

    public void fillYear(String year) {
        page.fill(YEAR_INPUT, year);
    }

    public void selectMonth(int month) {
        page.selectOption(MONTH_SELECT, String.valueOf(month));
    }

    public void clickSubmit() {
        page.click(SUBMIT_BUTTON);
        page.waitForLoadState();
    }
}
