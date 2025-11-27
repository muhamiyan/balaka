package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.FiscalPeriodDetailPage;
import com.artivisi.accountingfinance.functional.page.FiscalPeriodFormPage;
import com.artivisi.accountingfinance.functional.page.FiscalPeriodListPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Fiscal Period Management (Section 2.7)")
class FiscalPeriodTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private FiscalPeriodListPage listPage;
    private FiscalPeriodFormPage formPage;
    private FiscalPeriodDetailPage detailPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        listPage = new FiscalPeriodListPage(page, baseUrl());
        formPage = new FiscalPeriodFormPage(page, baseUrl());
        detailPage = new FiscalPeriodDetailPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("2.7.1 Fiscal Period List")
    class FiscalPeriodListTests {

        @Test
        @DisplayName("Should display fiscal period list page")
        void shouldDisplayFiscalPeriodListPage() {
            listPage.navigate();

            listPage.assertPageTitleVisible();
            listPage.assertPageTitleText("Periode Fiskal");
        }

        @Test
        @DisplayName("Should display period table")
        void shouldDisplayPeriodTable() {
            listPage.navigate();

            listPage.assertTableVisible();
        }
    }

    @Nested
    @DisplayName("2.7.2 Fiscal Period Form")
    class FiscalPeriodFormTests {

        @Test
        @DisplayName("Should display new period form")
        void shouldDisplayNewPeriodForm() {
            formPage.navigateToNew();

            formPage.assertPageTitleText("Periode Fiskal Baru");
        }

        @Test
        @DisplayName("Should navigate to form from list page")
        void shouldNavigateToFormFromListPage() {
            listPage.navigate();
            listPage.clickNewPeriodButton();

            formPage.assertPageTitleText("Periode Fiskal Baru");
        }
    }

    @Nested
    @DisplayName("2.7.3 Fiscal Period CRUD")
    class FiscalPeriodCrudTests {

        @Test
        @DisplayName("Should create new fiscal period")
        void shouldCreateNewFiscalPeriod() {
            formPage.navigateToNew();

            // Use a unique year-month combination
            int testYear = 2099;
            int testMonth = 1;

            formPage.fillYear(String.valueOf(testYear));
            formPage.selectMonth(testMonth);
            formPage.clickSubmit();

            // Should redirect to detail page
            detailPage.assertPeriodNameText("Januari " + testYear);
            detailPage.assertPeriodCodeText(testYear + "-01");
        }

        @Test
        @DisplayName("Should show period in list after creation")
        void shouldShowPeriodInListAfterCreation() {
            formPage.navigateToNew();

            int testYear = 2098;
            int testMonth = 6;

            formPage.fillYear(String.valueOf(testYear));
            formPage.selectMonth(testMonth);
            formPage.clickSubmit();

            // Navigate to list
            listPage.navigate();

            assertThat(listPage.hasPeriodWithName("Juni " + testYear)).isTrue();
        }
    }

    @Nested
    @DisplayName("2.7.4 Fiscal Period Status Workflow")
    class FiscalPeriodStatusTests {

        @Test
        @DisplayName("Should close month for open period")
        void shouldCloseMonthForOpenPeriod() {
            // Create a period first
            formPage.navigateToNew();

            int testYear = 2097;
            int testMonth = 3;

            formPage.fillYear(String.valueOf(testYear));
            formPage.selectMonth(testMonth);
            formPage.clickSubmit();

            // Should be open by default
            detailPage.assertStatusText("Terbuka");
            assertThat(detailPage.hasCloseMonthButton()).isTrue();

            // Close month
            detailPage.clickCloseMonthButton();

            // Should show month closed status
            detailPage.assertStatusText("Tutup Bulan");
            assertThat(detailPage.hasFileTaxButton()).isTrue();
            assertThat(detailPage.hasReopenButton()).isTrue();
        }

        @Test
        @DisplayName("Should file tax for month-closed period")
        void shouldFileTaxForMonthClosedPeriod() {
            // Create and close a period
            formPage.navigateToNew();

            int testYear = 2096;
            int testMonth = 4;

            formPage.fillYear(String.valueOf(testYear));
            formPage.selectMonth(testMonth);
            formPage.clickSubmit();

            detailPage.clickCloseMonthButton();
            detailPage.assertStatusText("Tutup Bulan");

            // File tax
            detailPage.clickFileTaxButton();

            // Should show tax filed status
            detailPage.assertStatusText("SPT Dilaporkan");
            // Should not have any action buttons
            assertThat(detailPage.hasCloseMonthButton()).isFalse();
            assertThat(detailPage.hasReopenButton()).isFalse();
        }

        @Test
        @DisplayName("Should reopen month-closed period")
        void shouldReopenMonthClosedPeriod() {
            // Create and close a period
            formPage.navigateToNew();

            int testYear = 2095;
            int testMonth = 5;

            formPage.fillYear(String.valueOf(testYear));
            formPage.selectMonth(testMonth);
            formPage.clickSubmit();

            detailPage.clickCloseMonthButton();
            detailPage.assertStatusText("Tutup Bulan");

            // Reopen
            detailPage.clickReopenButton();

            // Should show open status
            detailPage.assertStatusText("Terbuka");
            assertThat(detailPage.hasCloseMonthButton()).isTrue();
        }
    }
}
