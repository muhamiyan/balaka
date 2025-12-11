package com.artivisi.accountingfinance.functional.manufacturing;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Manufacturing BOM Tests
 * Tests Bill of Materials viewing and management.
 * Data from V830: BOM-KSGA (Kopi Susu Gula Aren), BOM-CRS (Croissant), etc.
 */
@DisplayName("Manufacturing - Bill of Materials")
public class MfgBomTest extends PlaywrightTestBase {

    @Test
    @DisplayName("Should display BOM list")
    void shouldDisplayBomList() {
        loginAsAdmin();
        navigateTo("/inventory/bom");
        waitForPageLoad();

        // Verify BOM list page loads
        assertThat(page.locator("h1")).containsText("Bill of Materials");
    }

    @Test
    @DisplayName("Should display drink BOMs")
    void shouldDisplayDrinkBoms() {
        loginAsAdmin();
        navigateTo("/inventory/bom");
        waitForPageLoad();

        // Verify drink BOMs from V830
        assertThat(page.locator("text=Kopi Susu Gula Aren")).isVisible();
        assertThat(page.locator("text=Es Kopi Susu")).isVisible();
        assertThat(page.locator("text=Americano")).isVisible();
    }

    @Test
    @DisplayName("Should display pastry BOMs")
    void shouldDisplayPastryBoms() {
        loginAsAdmin();
        navigateTo("/inventory/bom");
        waitForPageLoad();

        // Verify pastry BOMs from V830
        assertThat(page.locator("text=Croissant")).isVisible();
        assertThat(page.locator("text=Roti Bakar Coklat")).isVisible();
    }

    @Test
    @DisplayName("Should display BOM detail with components - Kopi Susu Gula Aren")
    void shouldDisplayBomDetailKopiSusu() {
        loginAsAdmin();
        navigateTo("/inventory/bom");
        waitForPageLoad();

        // Click on Kopi Susu Gula Aren BOM
        page.locator("a:has-text('Kopi Susu Gula Aren')").first().click();
        waitForPageLoad();

        // Verify BOM detail page shows components
        assertThat(page.locator("text=Biji Kopi Arabica")).isVisible();
        assertThat(page.locator("text=Susu Segar")).isVisible();
        assertThat(page.locator("text=Gula Aren")).isVisible();
    }

    @Test
    @DisplayName("Should display BOM detail with components - Croissant")
    void shouldDisplayBomDetailCroissant() {
        loginAsAdmin();
        navigateTo("/inventory/bom");
        waitForPageLoad();

        // Click on Croissant BOM
        page.locator("a:has-text('Croissant')").first().click();
        waitForPageLoad();

        // Verify BOM detail page shows components
        assertThat(page.locator("text=Tepung Terigu")).isVisible();
        assertThat(page.locator("text=Butter")).isVisible();
        assertThat(page.locator("text=Telur")).isVisible();
    }

    @Test
    @DisplayName("Should show output quantity in BOM detail")
    void shouldShowOutputQuantityInBomDetail() {
        loginAsAdmin();
        navigateTo("/inventory/bom");
        waitForPageLoad();

        // Click on Croissant BOM (batch of 12)
        page.locator("a:has-text('Croissant')").first().click();
        waitForPageLoad();

        // Verify output quantity is shown (12 for croissant batch)
        assertThat(page.locator("text=12")).isVisible();
    }
}
