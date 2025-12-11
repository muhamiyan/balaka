package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Page Object for Employee List (/employees).
 */
public class EmployeeListPage {

    private final Page page;
    private final String baseUrl;

    // Locators - using IDs only
    private static final String PAGE_TITLE = "#page-title";
    private static final String SEARCH_INPUT = "#search-input";
    private static final String BTN_NEW_EMPLOYEE = "#btn-new-employee";
    private static final String EMPLOYEE_TABLE = "#employee-table";

    public EmployeeListPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public EmployeeListPage navigate() {
        page.navigate(baseUrl + "/employees");
        page.waitForLoadState();
        return this;
    }

    public EmployeeListPage verifyPageTitle() {
        assertThat(page.locator(PAGE_TITLE)).containsText("Karyawan");
        return this;
    }

    public EmployeeListPage search(String query) {
        page.locator(SEARCH_INPUT).fill(query);
        page.waitForLoadState();
        return this;
    }

    public EmployeeListPage clickNewEmployee() {
        page.locator(BTN_NEW_EMPLOYEE).click();
        page.waitForLoadState();
        return this;
    }

    /**
     * Verify exact number of employees in the table.
     */
    public EmployeeListPage verifyEmployeeCount(int expectedCount) {
        assertThat(page.locator(EMPLOYEE_TABLE + " tbody tr")).hasCount(expectedCount);
        return this;
    }

    /**
     * Verify minimum number of employees.
     */
    public EmployeeListPage verifyMinimumEmployeeCount(int minCount) {
        int count = page.locator(EMPLOYEE_TABLE + " tbody tr").count();
        if (count < minCount) {
            throw new AssertionError("Expected at least " + minCount + " employees, but found " + count);
        }
        return this;
    }

    /**
     * Verify employee table is visible.
     */
    public EmployeeListPage verifyTableVisible() {
        assertThat(page.locator(EMPLOYEE_TABLE)).isVisible();
        return this;
    }

    public EmployeeListPage takeScreenshot(String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(path)));
        return this;
    }
}
