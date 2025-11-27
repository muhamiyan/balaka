package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static org.assertj.core.api.Assertions.assertThat;

public class FiscalPeriodDetailPage {
    private final Page page;
    private final String baseUrl;

    private static final String PAGE_TITLE = "#page-title";
    private static final String PERIOD_DETAIL = "[data-testid='period-detail']";
    private static final String PERIOD_NAME = "[data-testid='period-name']";
    private static final String PERIOD_CODE = "[data-testid='period-code']";
    private static final String PERIOD_STATUS = "[data-testid='period-status']";
    private static final String CLOSE_MONTH_BUTTON = "[data-testid='btn-close-month']";
    private static final String FILE_TAX_BUTTON = "[data-testid='btn-file-tax']";
    private static final String REOPEN_BUTTON = "[data-testid='btn-reopen']";

    public FiscalPeriodDetailPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public FiscalPeriodDetailPage navigate(String periodId) {
        page.navigate(baseUrl + "/fiscal-periods/" + periodId);
        return this;
    }

    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE).isVisible()).isTrue();
    }

    public void assertPeriodNameText(String expected) {
        assertThat(page.locator(PERIOD_NAME).textContent()).contains(expected);
    }

    public void assertPeriodCodeText(String expected) {
        assertThat(page.locator(PERIOD_CODE).textContent()).contains(expected);
    }

    public void assertStatusText(String expected) {
        assertThat(page.locator(PERIOD_STATUS).textContent()).contains(expected);
    }

    public boolean hasCloseMonthButton() {
        return page.locator(CLOSE_MONTH_BUTTON).count() > 0;
    }

    public boolean hasFileTaxButton() {
        return page.locator(FILE_TAX_BUTTON).count() > 0;
    }

    public boolean hasReopenButton() {
        return page.locator(REOPEN_BUTTON).count() > 0;
    }

    public void clickCloseMonthButton() {
        page.onceDialog(dialog -> dialog.accept());
        page.click(CLOSE_MONTH_BUTTON);
        page.waitForLoadState();
    }

    public void clickFileTaxButton() {
        page.onceDialog(dialog -> dialog.accept());
        page.click(FILE_TAX_BUTTON);
        page.waitForLoadState();
    }

    public void clickReopenButton() {
        page.onceDialog(dialog -> dialog.accept());
        page.click(REOPEN_BUTTON);
        page.waitForLoadState();
    }
}
