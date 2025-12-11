package com.artivisi.accountingfinance.functional.manufacturing;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Manufacturing Production Order Tests
 * Tests production order viewing and management.
 * Data from V831: PROD-001 (Croissant), PROD-002 (Roti Bakar Coklat)
 */
@DisplayName("Manufacturing - Production Orders")
public class MfgProductionTest extends PlaywrightTestBase {

    @Test
    @DisplayName("Should display production order list")
    void shouldDisplayProductionOrderList() {
        loginAsAdmin();
        navigateTo("/inventory/production");
        waitForPageLoad();

        // Verify production order list page loads
        assertThat(page.locator("h1")).containsText("Production Order");
    }

    @Test
    @DisplayName("Should display completed production orders")
    void shouldDisplayCompletedProductionOrders() {
        loginAsAdmin();
        navigateTo("/inventory/production");
        waitForPageLoad();

        // Verify production orders from V831
        assertThat(page.locator("text=PROD-001")).isVisible();
        assertThat(page.locator("text=PROD-002")).isVisible();
    }

    @Test
    @DisplayName("Should display production order detail - Croissant")
    void shouldDisplayProductionOrderDetailCroissant() {
        loginAsAdmin();
        navigateTo("/inventory/production");
        waitForPageLoad();

        // Click on PROD-001
        page.locator("a:has-text('PROD-001')").first().click();
        waitForPageLoad();

        // Verify production order detail
        assertThat(page.locator("text=PROD-001")).isVisible();
        assertThat(page.locator("text=Croissant")).isVisible();
        assertThat(page.locator("text=COMPLETED")).isVisible();
    }

    @Test
    @DisplayName("Should display production order detail - Roti Bakar Coklat")
    void shouldDisplayProductionOrderDetailRotiCoklat() {
        loginAsAdmin();
        navigateTo("/inventory/production");
        waitForPageLoad();

        // Click on PROD-002
        page.locator("a:has-text('PROD-002')").first().click();
        waitForPageLoad();

        // Verify production order detail
        assertThat(page.locator("text=PROD-002")).isVisible();
        assertThat(page.locator("text=Roti Bakar Coklat")).isVisible();
        assertThat(page.locator("text=COMPLETED")).isVisible();
    }

    @Test
    @DisplayName("Should show production quantity in order detail")
    void shouldShowProductionQuantityInOrderDetail() {
        loginAsAdmin();
        navigateTo("/inventory/production");
        waitForPageLoad();

        // Click on PROD-001 (24 croissants)
        page.locator("a:has-text('PROD-001')").first().click();
        waitForPageLoad();

        // Verify quantity is shown (24 for croissant production)
        assertThat(page.locator("text=24")).isVisible();
    }

    @Test
    @DisplayName("Should filter production orders by status")
    void shouldFilterProductionOrdersByStatus() {
        loginAsAdmin();
        navigateTo("/inventory/production?status=COMPLETED");
        waitForPageLoad();

        // Verify completed orders are shown
        assertThat(page.locator("text=PROD-001")).isVisible();
        assertThat(page.locator("text=PROD-002")).isVisible();
    }
}
