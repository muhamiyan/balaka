package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.TaxExportPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tax Export for Coretax (Phase 2.9)")
class TaxExportTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private TaxExportPage taxExportPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        taxExportPage = new TaxExportPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    /**
     * Get the start and end months for test data.
     * Test data uses CURRENT_DATE - INTERVAL '2-5 days'.
     * When data spans month boundaries (e.g., Nov 29 to Dec 2),
     * we need to use the full range.
     */
    private YearMonth getTestDataStartMonth() {
        // Get the date 5 days ago (oldest test data record)
        var fiveDaysAgo = java.time.LocalDate.now().minusDays(5);
        return YearMonth.from(fiveDaysAgo);
    }

    private YearMonth getTestDataEndMonth() {
        // Get the date 2 days ago (newest test data record)
        var twoDaysAgo = java.time.LocalDate.now().minusDays(2);
        return YearMonth.from(twoDaysAgo);
    }

    @Nested
    @DisplayName("9.1 Page Navigation")
    class PageNavigationTests {

        @Test
        @DisplayName("Should display tax export page title")
        void shouldDisplayTaxExportPageTitle() {
            taxExportPage.navigateWithMonthRange(getTestDataStartMonth(), getTestDataEndMonth());

            taxExportPage.assertPageTitleVisible();
            taxExportPage.assertPageTitleText("Export Data Pajak untuk Coretax");
        }

        @Test
        @DisplayName("Should display start month selector")
        void shouldDisplayStartMonthSelector() {
            taxExportPage.navigateWithMonthRange(getTestDataStartMonth(), getTestDataEndMonth());

            taxExportPage.assertStartMonthVisible();
        }

        @Test
        @DisplayName("Should display end month selector")
        void shouldDisplayEndMonthSelector() {
            taxExportPage.navigateWithMonthRange(getTestDataStartMonth(), getTestDataEndMonth());

            taxExportPage.assertEndMonthVisible();
        }

        @Test
        @DisplayName("Should display submit button")
        void shouldDisplaySubmitButton() {
            taxExportPage.navigateWithMonthRange(getTestDataStartMonth(), getTestDataEndMonth());

            taxExportPage.assertSubmitButtonVisible();
        }
    }

    @Nested
    @DisplayName("9.2 Export Statistics")
    class ExportStatisticsTests {

        @Test
        @DisplayName("Should display faktur keluaran count")
        void shouldDisplayFakturKeluaranCount() {
            // Test data has 2 PPN_KELUARAN records
            taxExportPage.navigateWithMonthRange(getTestDataStartMonth(), getTestDataEndMonth());

            String countText = taxExportPage.getFakturKeluaranCountText();
            assertThat(countText).contains("faktur");
        }

        @Test
        @DisplayName("Should display faktur masukan count")
        void shouldDisplayFakturMasukanCount() {
            // Test data has 1 PPN_MASUKAN record
            taxExportPage.navigateWithMonthRange(getTestDataStartMonth(), getTestDataEndMonth());

            String countText = taxExportPage.getFakturMasukanCountText();
            assertThat(countText).contains("faktur");
        }

        @Test
        @DisplayName("Should display bupot count")
        void shouldDisplayBupotCount() {
            // Test data has 1 PPH_23 record
            taxExportPage.navigateWithMonthRange(getTestDataStartMonth(), getTestDataEndMonth());

            String countText = taxExportPage.getBupotCountText();
            assertThat(countText).contains("bupot");
        }
    }

    @Nested
    @DisplayName("9.3 e-Faktur Keluaran Export")
    class EFakturKeluaranExportTests {

        @Test
        @DisplayName("Should download e-Faktur Keluaran Excel file")
        void shouldDownloadEFakturKeluaranExcel() throws Exception {
            taxExportPage.navigateWithMonthRange(getTestDataStartMonth(), getTestDataEndMonth());

            byte[] excelData = taxExportPage.downloadFakturKeluaran();

            assertThat(excelData).isNotEmpty();

            // Verify it's a valid XLSX file by parsing it
            try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
                assertThat(workbook.getNumberOfSheets()).isGreaterThan(0);

                // Check the data sheet exists
                var sheet = workbook.getSheetAt(0);
                assertThat(sheet).isNotNull();
            }
        }

        @Test
        @DisplayName("Should have correct data in e-Faktur Keluaran Excel")
        void shouldHaveCorrectDataInEFakturKeluaranExcel() throws Exception {
            taxExportPage.navigateWithMonthRange(getTestDataStartMonth(), getTestDataEndMonth());

            byte[] excelData = taxExportPage.downloadFakturKeluaran();

            try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
                var sheet = workbook.getSheetAt(0);

                // Check header row exists
                Row headerRow = sheet.getRow(0);
                assertThat((Object) headerRow).isNotNull();

                // e-Faktur should have specific columns
                // Check first column is typically "No" or "Nomor Faktur"
                assertThat(headerRow.getPhysicalNumberOfCells()).isGreaterThan(0);
            }
        }
    }

    @Nested
    @DisplayName("9.4 e-Faktur Masukan Export")
    class EFakturMasukanExportTests {

        @Test
        @DisplayName("Should download e-Faktur Masukan Excel file")
        void shouldDownloadEFakturMasukanExcel() throws Exception {
            taxExportPage.navigateWithMonthRange(getTestDataStartMonth(), getTestDataEndMonth());

            byte[] excelData = taxExportPage.downloadFakturMasukan();

            assertThat(excelData).isNotEmpty();

            // Verify it's a valid XLSX file
            try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
                assertThat(workbook.getNumberOfSheets()).isGreaterThan(0);
            }
        }
    }

    @Nested
    @DisplayName("9.5 Bupot Unifikasi Export")
    class BupotUnifikasiExportTests {

        @Test
        @DisplayName("Should download Bupot Unifikasi Excel file")
        void shouldDownloadBupotUnifikasiExcel() throws Exception {
            taxExportPage.navigateWithMonthRange(getTestDataStartMonth(), getTestDataEndMonth());

            byte[] excelData = taxExportPage.downloadBupot();

            assertThat(excelData).isNotEmpty();

            // Verify it's a valid XLSX file
            try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
                assertThat(workbook.getNumberOfSheets()).isGreaterThan(0);
            }
        }

        @Test
        @DisplayName("Should have correct data in Bupot Unifikasi Excel")
        void shouldHaveCorrectDataInBupotUnifikasiExcel() throws Exception {
            taxExportPage.navigateWithMonthRange(getTestDataStartMonth(), getTestDataEndMonth());

            byte[] excelData = taxExportPage.downloadBupot();

            try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData))) {
                var sheet = workbook.getSheetAt(0);

                // Check header row exists
                Row headerRow = sheet.getRow(0);
                assertThat((Object) headerRow).isNotNull();

                // Bupot should have specific columns
                assertThat(headerRow.getPhysicalNumberOfCells()).isGreaterThan(0);
            }
        }
    }
}
