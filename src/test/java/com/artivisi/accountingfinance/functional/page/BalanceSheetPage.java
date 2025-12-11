package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Balance Sheet Report (/reports/balance-sheet).
 * Handles viewing and verifying balance sheet (neraca) report.
 */
public class BalanceSheetPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs only
    private static final String PAGE_TITLE = "#page-title";
    private static final String REPORT_TITLE = "#report-title";
    private static final String REPORT_DATE = "#report-date";
    private static final String AS_OF_DATE = "#asOfDate";
    private static final String BTN_GENERATE = "#btn-generate";
    private static final String BTN_EXPORT_PDF = "#btn-export-pdf";
    private static final String BTN_EXPORT_EXCEL = "#btn-export-excel";
    private static final String BTN_PRINT = "#btn-print";
    private static final String ASSET_ITEMS = "#asset-items";
    private static final String LIABILITY_ITEMS = "#liability-items";
    private static final String EQUITY_ITEMS = "#equity-items";
    private static final String TOTAL_ASSETS = "#total-assets";
    private static final String TOTAL_LIABILITIES = "#total-liabilities";
    private static final String TOTAL_EQUITY = "#total-equity";
    private static final String TOTAL_LIABILITIES_EQUITY = "#total-liabilities-equity";
    private static final String CURRENT_YEAR_EARNINGS = "#current-year-earnings";
    private static final String BALANCE_STATUS = "#balance-status";
    private static final String BALANCE_MESSAGE = "#balance-message";

    public BalanceSheetPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    /**
     * Navigate to balance sheet report.
     */
    public BalanceSheetPage navigate() {
        page.navigate(baseUrl + "/reports/balance-sheet");
        page.waitForLoadState();
        return this;
    }

    /**
     * Navigate to balance sheet report with specific date.
     */
    public BalanceSheetPage navigate(String asOfDate) {
        page.navigate(baseUrl + "/reports/balance-sheet?asOfDate=" + asOfDate);
        page.waitForLoadState();
        return this;
    }

    /**
     * Navigate with year and month (uses last day of month).
     */
    public BalanceSheetPage navigateWithYearMonth(int year, int month) {
        int lastDay = java.time.YearMonth.of(year, month).lengthOfMonth();
        String asOfDate = String.format("%d-%02d-%02d", year, month, lastDay);
        return navigate(asOfDate);
    }

    /**
     * Fill date and generate report.
     */
    public BalanceSheetPage generateReport(String asOfDate) {
        page.locator(AS_OF_DATE).fill(asOfDate);
        page.locator(BTN_GENERATE).click();
        page.waitForLoadState();
        return this;
    }

    /**
     * Verify page title.
     */
    public BalanceSheetPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Neraca");
        return this;
    }

    /**
     * Verify report title.
     */
    public BalanceSheetPage verifyReportTitle() {
        assertThat(page.locator(REPORT_TITLE)).containsText("LAPORAN POSISI KEUANGAN");
        return this;
    }

    /**
     * Verify asset section is visible.
     */
    public BalanceSheetPage verifyAssetSectionVisible() {
        assertThat(page.locator(ASSET_ITEMS)).isVisible();
        return this;
    }

    /**
     * Verify liability section is visible.
     */
    public BalanceSheetPage verifyLiabilitySectionVisible() {
        assertThat(page.locator(LIABILITY_ITEMS)).isVisible();
        return this;
    }

    /**
     * Verify equity section is visible.
     */
    public BalanceSheetPage verifyEquitySectionVisible() {
        assertThat(page.locator(EQUITY_ITEMS)).isVisible();
        return this;
    }

    /**
     * Verify balance status shows "Neraca Balance".
     */
    public BalanceSheetPage verifyBalanced() {
        assertThat(page.locator(BALANCE_STATUS)).containsText("Neraca Balance");
        return this;
    }

    /**
     * Get total assets text.
     */
    public String getTotalAssets() {
        return page.locator(TOTAL_ASSETS).textContent();
    }

    /**
     * Get total liabilities text.
     */
    public String getTotalLiabilities() {
        return page.locator(TOTAL_LIABILITIES).textContent();
    }

    /**
     * Get total equity text.
     */
    public String getTotalEquity() {
        return page.locator(TOTAL_EQUITY).textContent();
    }

    /**
     * Get total liabilities + equity text.
     */
    public String getTotalLiabilitiesEquity() {
        return page.locator(TOTAL_LIABILITIES_EQUITY).textContent();
    }

    /**
     * Verify total assets amount.
     */
    public BalanceSheetPage verifyTotalAssets(String expectedAmount) {
        assertThat(page.locator(TOTAL_ASSETS)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify total liabilities amount.
     */
    public BalanceSheetPage verifyTotalLiabilities(String expectedAmount) {
        assertThat(page.locator(TOTAL_LIABILITIES)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify total equity amount.
     */
    public BalanceSheetPage verifyTotalEquity(String expectedAmount) {
        assertThat(page.locator(TOTAL_EQUITY)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify total liabilities + equity amount.
     */
    public BalanceSheetPage verifyTotalLiabilitiesEquity(String expectedAmount) {
        assertThat(page.locator(TOTAL_LIABILITIES_EQUITY)).containsText(expectedAmount);
        return this;
    }

    /**
     * Verify current year earnings amount.
     */
    public BalanceSheetPage verifyCurrentYearEarnings(String expectedAmount) {
        assertThat(page.locator(CURRENT_YEAR_EARNINGS)).containsText(expectedAmount);
        return this;
    }

    /**
     * Take screenshot.
     */
    public BalanceSheetPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
