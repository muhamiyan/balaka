package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Client List (/clients).
 */
public class ClientListPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs only
    private static final String PAGE_TITLE = "#page-title";
    private static final String SEARCH_INPUT = "#search-input";
    private static final String BTN_NEW_CLIENT = "#btn-new-client";
    private static final String CLIENT_TABLE = "#client-table";

    public ClientListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public ClientListPage navigate() {
        page.navigate(baseUrl + "/clients");
        page.waitForLoadState();
        return this;
    }

    public ClientListPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Klien");
        return this;
    }

    public ClientListPage search(String query) {
        page.locator(SEARCH_INPUT).fill(query);
        page.waitForLoadState();
        return this;
    }

    public ClientListPage clickNewClient() {
        page.locator(BTN_NEW_CLIENT).click();
        page.waitForLoadState();
        return this;
    }

    /**
     * Verify exact number of clients in the table.
     */
    public ClientListPage verifyClientCount(int expectedCount) {
        assertThat(page.locator(CLIENT_TABLE + " tbody tr")).hasCount(expectedCount);
        return this;
    }

    /**
     * Verify minimum number of clients.
     */
    public ClientListPage verifyMinimumClientCount(int minCount) {
        int count = page.locator(CLIENT_TABLE + " tbody tr").count();
        if (count < minCount) {
            throw new AssertionError("Expected at least " + minCount + " clients, but found " + count);
        }
        return this;
    }

    /**
     * Verify client table is visible.
     */
    public ClientListPage verifyTableVisible() {
        assertThat(page.locator(CLIENT_TABLE)).isVisible();
        return this;
    }

    public ClientListPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
