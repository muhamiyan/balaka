package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Inventory Stock (/inventory/stock).
 * Handles stock balance display and verification.
 */
public class InventoryStockPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs
    private static final String PAGE_TITLE = "#page-title";
    private static final String STOCK_TABLE = "#stock-table";
    private static final String SEARCH_INPUT = "#search-input";
    private static final String CATEGORY_FILTER = "#category-filter";

    public InventoryStockPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public InventoryStockPage navigate() {
        page.navigate(baseUrl + "/inventory/stock");
        page.waitForLoadState();
        return this;
    }

    public InventoryStockPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Stok Barang");
        return this;
    }

    public InventoryStockPage verifyTableVisible() {
        assertThat(page.locator(STOCK_TABLE)).isVisible();
        return this;
    }

    public InventoryStockPage verifyProductCount(int expectedCount) {
        assertThat(page.locator(STOCK_TABLE + " tbody tr")).hasCount(expectedCount);
        return this;
    }

    /**
     * Verify product stock exists by product code.
     */
    public InventoryStockPage verifyProductStockExists(String productCode) {
        String rowSelector = "#stock-row-" + productCode;
        int count = page.locator(rowSelector).count();
        if (count == 0) {
            // Fallback: check if product code text is visible in table
            assertThat(page.locator(STOCK_TABLE + " td:has-text('" + productCode + "')").first()).isVisible();
        }
        return this;
    }

    /**
     * Verify stock quantity for a product.
     * @param productCode the product code
     * @param expectedQuantity expected quantity as string (e.g., "5")
     */
    public InventoryStockPage verifyStockQuantity(String productCode, String expectedQuantity) {
        String quantitySelector = "#stock-qty-" + productCode;
        int count = page.locator(quantitySelector).count();
        if (count > 0) {
            assertThat(page.locator(quantitySelector)).containsText(expectedQuantity);
        } else {
            // Fallback: find row and check quantity column
            String row = STOCK_TABLE + " tr:has-text('" + productCode + "')";
            assertThat(page.locator(row).first()).containsText(expectedQuantity);
        }
        return this;
    }

    public InventoryStockPage search(String query) {
        page.locator(SEARCH_INPUT).fill(query);
        page.waitForTimeout(500);
        return this;
    }

    public InventoryStockPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
