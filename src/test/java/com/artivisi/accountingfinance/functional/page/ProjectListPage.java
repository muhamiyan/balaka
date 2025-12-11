package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Project List (/projects).
 */
public class ProjectListPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs only
    private static final String PAGE_TITLE = "#page-title";
    private static final String SEARCH_INPUT = "#search-input";
    private static final String BTN_NEW_PROJECT = "#btn-new-project";
    private static final String PROJECT_TABLE = "#project-table";

    public ProjectListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public ProjectListPage navigate() {
        page.navigate(baseUrl + "/projects");
        page.waitForLoadState();
        return this;
    }

    public ProjectListPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Proyek");
        return this;
    }

    public ProjectListPage search(String query) {
        page.locator(SEARCH_INPUT).fill(query);
        page.waitForLoadState();
        return this;
    }

    public ProjectListPage clickNewProject() {
        page.locator(BTN_NEW_PROJECT).click();
        page.waitForLoadState();
        return this;
    }

    /**
     * Verify exact number of projects in the table.
     */
    public ProjectListPage verifyProjectCount(int expectedCount) {
        assertThat(page.locator(PROJECT_TABLE + " tbody tr")).hasCount(expectedCount);
        return this;
    }

    /**
     * Verify minimum number of projects.
     */
    public ProjectListPage verifyMinimumProjectCount(int minCount) {
        int count = page.locator(PROJECT_TABLE + " tbody tr").count();
        if (count < minCount) {
            throw new AssertionError("Expected at least " + minCount + " projects, but found " + count);
        }
        return this;
    }

    /**
     * Verify project table is visible.
     */
    public ProjectListPage verifyTableVisible() {
        assertThat(page.locator(PROJECT_TABLE)).isVisible();
        return this;
    }

    public ProjectListPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
