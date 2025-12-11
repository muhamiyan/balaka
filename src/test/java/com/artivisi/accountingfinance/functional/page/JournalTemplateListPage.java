package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Journal Template List (/templates).
 */
public class JournalTemplateListPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs and data-testid
    private static final String PAGE_TITLE = "#page-title";
    private static final String TEMPLATE_LIST = "[data-testid='template-list']";
    private static final String TEMPLATE_GRID = "#template-grid";
    private static final String SEARCH_INPUT = "[data-testid='search-input']";
    private static final String SEARCH_BUTTON = "[data-testid='search-button']";
    private static final String BTN_NEW_TEMPLATE = "#btn-new-template";
    private static final String FILTER_FAVORITES = "[data-testid='filter-favorites']";

    public JournalTemplateListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public JournalTemplateListPage navigate() {
        page.navigate(baseUrl + "/templates");
        page.waitForLoadState();
        return this;
    }

    public JournalTemplateListPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Template");
        return this;
    }

    public JournalTemplateListPage verifyContentVisible() {
        assertThat(page.locator(TEMPLATE_LIST)).isVisible();
        return this;
    }

    public JournalTemplateListPage search(String query) {
        page.locator(SEARCH_INPUT).fill(query);
        page.locator(SEARCH_BUTTON).click();
        page.waitForLoadState();
        return this;
    }

    public JournalTemplateListPage filterFavorites() {
        page.locator(FILTER_FAVORITES).click();
        page.waitForLoadState();
        return this;
    }

    public JournalTemplateListPage clickNewTemplate() {
        page.locator(BTN_NEW_TEMPLATE).click();
        page.waitForLoadState();
        return this;
    }

    public JournalTemplateListPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
