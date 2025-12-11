package com.artivisi.accountingfinance.functional.manufacturing;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Manufacturing Materials Tests
 * Tests raw material management for coffee shop.
 * Data from V830: Biji Kopi Arabica, Susu Segar, Gula Aren, Tepung, Butter, etc.
 */
@DisplayName("Manufacturing - Raw Materials")
public class MfgMaterialsTest extends PlaywrightTestBase {

    @Test
    @DisplayName("Should display coffee shop raw materials")
    void shouldDisplayCoffeeShopRawMaterials() {
        loginAsAdmin();
        navigateTo("/products");
        waitForPageLoad();

        // Verify products page loads
        assertThat(page.locator("h1")).containsText("Produk");

        // Verify coffee raw materials from V830
        assertThat(page.locator("text=Biji Kopi Arabica")).isVisible();
        assertThat(page.locator("text=Susu Segar")).isVisible();
    }

    @Test
    @DisplayName("Should display raw material categories")
    void shouldDisplayRawMaterialCategories() {
        loginAsAdmin();
        navigateTo("/products/categories");
        waitForPageLoad();

        // Verify categories page loads
        assertThat(page.locator("h1")).containsText("Kategori");

        // Verify coffee shop categories from V830
        assertThat(page.locator("text=Bahan Baku Kopi")).isVisible();
        assertThat(page.locator("text=Bahan Baku Roti")).isVisible();
    }

    @Test
    @DisplayName("Should display finished goods - drinks")
    void shouldDisplayFinishedGoodsDrinks() {
        loginAsAdmin();
        navigateTo("/products");
        waitForPageLoad();

        // Verify finished goods drinks from V830
        assertThat(page.locator("text=Kopi Susu Gula Aren")).isVisible();
        assertThat(page.locator("text=Es Kopi Susu")).isVisible();
        assertThat(page.locator("text=Americano")).isVisible();
    }

    @Test
    @DisplayName("Should display finished goods - pastries")
    void shouldDisplayFinishedGoodsPastries() {
        loginAsAdmin();
        navigateTo("/products");
        waitForPageLoad();

        // Verify finished goods pastries from V830
        assertThat(page.locator("text=Croissant")).isVisible();
        assertThat(page.locator("text=Roti Bakar Coklat")).isVisible();
    }

    @Test
    @DisplayName("Should display raw material stock after purchases")
    void shouldDisplayRawMaterialStockAfterPurchases() {
        loginAsAdmin();
        navigateTo("/inventory/stock");
        waitForPageLoad();

        // Verify stock page loads
        assertThat(page.locator("h1")).containsText("Stok Barang");

        // Verify raw materials have stock from V831 purchases
        // Kopi Arabica: purchased 10kg, used some for production
        assertThat(page.locator("text=Biji Kopi Arabica")).isVisible();
    }
}
