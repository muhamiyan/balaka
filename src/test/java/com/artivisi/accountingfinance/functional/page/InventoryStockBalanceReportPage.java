package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

public class InventoryStockBalanceReportPage {

    private final Page page;
    private final String baseUrl;

    public InventoryStockBalanceReportPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void navigate() {
        page.navigate(baseUrl + "/inventory/reports/stock-balance");
        page.waitForLoadState();
    }

    public void assertPageTitleVisible() {
        page.locator("h1:has-text('Laporan Saldo Stok')").waitFor();
    }

    public void assertPageTitleText(String expectedText) {
        page.locator("h1:has-text('" + expectedText + "')").isVisible();
    }

    public void assertTableVisible() {
        page.locator("table").waitFor();
    }

    public void assertExportPdfButtonVisible() {
        page.locator("a:has-text('PDF')").waitFor();
    }

    public void assertExportExcelButtonVisible() {
        page.locator("a:has-text('Excel')").waitFor();
    }

    public void assertPrintButtonVisible() {
        page.locator("a:has-text('Cetak')").waitFor();
    }

    public int getRowCount() {
        page.locator("table tbody tr").first().waitFor();
        return page.locator("table tbody tr").count();
    }

    public String getTotalValueText() {
        return page.locator(".text-primary-600:has-text('Rp')").textContent().replace("Rp ", "").trim();
    }

    public boolean hasProduct(String productCode) {
        return page.locator("td:has-text('" + productCode + "')").count() > 0;
    }

    public String getExportPdfUrl() {
        return page.locator("a:has-text('PDF')").getAttribute("href");
    }

    public String getExportExcelUrl() {
        return page.locator("a:has-text('Excel')").getAttribute("href");
    }
}
