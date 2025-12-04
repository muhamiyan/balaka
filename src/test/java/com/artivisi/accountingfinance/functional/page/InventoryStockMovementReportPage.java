package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

public class InventoryStockMovementReportPage {

    private final Page page;
    private final String baseUrl;

    public InventoryStockMovementReportPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void navigate() {
        page.navigate(baseUrl + "/inventory/reports/stock-movement");
        page.waitForLoadState();
    }

    public void navigateWithParams(String startDate, String endDate) {
        page.navigate(baseUrl + "/inventory/reports/stock-movement?startDate=" + startDate + "&endDate=" + endDate);
        page.waitForLoadState();
    }

    public void assertPageTitleVisible() {
        page.locator("h1:has-text('Laporan Mutasi Stok')").waitFor();
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

    public String getTotalInboundValueText() {
        return page.locator(".text-green-600:has-text('Rp')").textContent().replace("Rp ", "").trim();
    }

    public String getTotalOutboundValueText() {
        return page.locator(".text-red-600:has-text('Rp')").textContent().replace("Rp ", "").trim();
    }

    public String getExportPdfUrl() {
        return page.locator("a:has-text('PDF')").getAttribute("href");
    }

    public String getExportExcelUrl() {
        return page.locator("a:has-text('Excel')").getAttribute("href");
    }
}
