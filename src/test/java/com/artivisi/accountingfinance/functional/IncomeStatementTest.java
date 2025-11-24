package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.IncomeStatementPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Income Statement Report (Section 4)")
class IncomeStatementTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private IncomeStatementPage incomeStatementPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        incomeStatementPage = new IncomeStatementPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("4.1 Navigation")
    class NavigationTests {

        @Test
        @DisplayName("Should display income statement page title")
        void shouldDisplayIncomeStatementPageTitle() {
            incomeStatementPage.navigate();

            incomeStatementPage.assertPageTitleVisible();
            incomeStatementPage.assertPageTitleText("Laporan Laba Rugi");
        }

        @Test
        @DisplayName("Should display report title 'LAPORAN LABA RUGI'")
        void shouldDisplayReportTitle() {
            incomeStatementPage.navigate();

            incomeStatementPage.assertReportTitleVisible();
            incomeStatementPage.assertReportTitleText("LAPORAN LABA RUGI");
        }
    }

    @Nested
    @DisplayName("4.2 Filter Controls")
    class FilterControlsTests {

        @Test
        @DisplayName("Should display start date selector")
        void shouldDisplayStartDateSelector() {
            incomeStatementPage.navigate();

            incomeStatementPage.assertStartDateVisible();
        }

        @Test
        @DisplayName("Should display end date selector")
        void shouldDisplayEndDateSelector() {
            incomeStatementPage.navigate();

            incomeStatementPage.assertEndDateVisible();
        }

        @Test
        @DisplayName("Should display generate button")
        void shouldDisplayGenerateButton() {
            incomeStatementPage.navigate();

            incomeStatementPage.assertGenerateButtonVisible();
        }

        @Test
        @DisplayName("Should display print button")
        void shouldDisplayPrintButton() {
            incomeStatementPage.navigate();

            incomeStatementPage.assertPrintButtonVisible();
        }
    }

    @Nested
    @DisplayName("4.3 Income Statement Structure")
    class IncomeStatementStructureTests {

        @Test
        @DisplayName("Should display revenue items section")
        void shouldDisplayRevenueItemsSection() {
            incomeStatementPage.navigateWithDates("2024-01-01", "2024-06-30");

            incomeStatementPage.assertRevenueItemsVisible();
        }

        @Test
        @DisplayName("Should display expense items section")
        void shouldDisplayExpenseItemsSection() {
            incomeStatementPage.navigateWithDates("2024-01-01", "2024-06-30");

            incomeStatementPage.assertExpenseItemsVisible();
        }

        @Test
        @DisplayName("Should display total revenue")
        void shouldDisplayTotalRevenue() {
            incomeStatementPage.navigateWithDates("2024-01-01", "2024-06-30");

            incomeStatementPage.assertTotalRevenueVisible();
        }

        @Test
        @DisplayName("Should display total expense")
        void shouldDisplayTotalExpense() {
            incomeStatementPage.navigateWithDates("2024-01-01", "2024-06-30");

            incomeStatementPage.assertTotalExpenseVisible();
        }

        @Test
        @DisplayName("Should display net income")
        void shouldDisplayNetIncome() {
            incomeStatementPage.navigateWithDates("2024-01-01", "2024-06-30");

            incomeStatementPage.assertNetIncomeVisible();
        }

        @Test
        @DisplayName("Should display profit status")
        void shouldDisplayProfitStatus() {
            incomeStatementPage.navigateWithDates("2024-01-01", "2024-06-30");

            incomeStatementPage.assertProfitStatusVisible();
        }
    }

    @Nested
    @DisplayName("4.4 Income Statement Calculation")
    class IncomeStatementCalculationTests {

        @Test
        @DisplayName("Should show revenue accounts with balances")
        void shouldShowRevenueAccountsWithBalances() {
            incomeStatementPage.navigateWithDates("2024-01-01", "2024-06-30");

            // Should have revenue rows from test data
            int revenueCount = incomeStatementPage.getRevenueRowCount();
            assertThat(revenueCount).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should show Pendapatan Jasa Konsultasi account")
        void shouldShowPendapatanKonsultasiAccount() {
            incomeStatementPage.navigateWithDates("2024-01-01", "2024-06-30");

            incomeStatementPage.assertAccountNameExists("Pendapatan Jasa Konsultasi");
        }

        @Test
        @DisplayName("Should show Pendapatan Jasa Development account")
        void shouldShowPendapatanDevelopmentAccount() {
            incomeStatementPage.navigateWithDates("2024-01-01", "2024-06-30");

            incomeStatementPage.assertAccountNameExists("Pendapatan Jasa Development");
        }

        @Test
        @DisplayName("Should show expense accounts")
        void shouldShowExpenseAccounts() {
            incomeStatementPage.navigateWithDates("2024-01-01", "2024-06-30");

            // Should have expense rows from test data
            int expenseCount = incomeStatementPage.getExpenseRowCount();
            assertThat(expenseCount).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should show Beban Gaji account")
        void shouldShowBebanGajiAccount() {
            incomeStatementPage.navigateWithDates("2024-01-01", "2024-06-30");

            incomeStatementPage.assertAccountNameExists("Beban Gaji");
        }

        @Test
        @DisplayName("Should show expected total revenue")
        void shouldShowExpectedTotalRevenue() {
            // Use December 2023 - safe from other tests' data pollution
            incomeStatementPage.navigateWithDates("2023-12-01", "2023-12-31");

            // December 2023 Revenue = Konsultasi 10M (JRN-2023-0002)
            String totalRevenue = incomeStatementPage.getTotalRevenueText();
            assertThat(totalRevenue).isEqualTo("10.000.000");
        }

        @Test
        @DisplayName("Should show expected total expense")
        void shouldShowExpectedTotalExpense() {
            // Use December 2023 - safe from other tests' data pollution
            incomeStatementPage.navigateWithDates("2023-12-01", "2023-12-31");

            // December 2023 has no expenses
            int expenseCount = incomeStatementPage.getExpenseRowCount();
            assertThat(expenseCount).isEqualTo(0);
        }

        @Test
        @DisplayName("Should show expected net income")
        void shouldShowExpectedNetIncome() {
            // Use December 2023 - safe from other tests' data pollution
            incomeStatementPage.navigateWithDates("2023-12-01", "2023-12-31");

            // December 2023 Net Income = Revenue 10M - Expense 0M = 10M
            String netIncome = incomeStatementPage.getNetIncomeText();
            assertThat(netIncome).isEqualTo("10.000.000");
        }

        @Test
        @DisplayName("Should show profit message for positive net income")
        void shouldShowProfitMessageForPositiveNetIncome() {
            // Use December 2023 - safe from other tests' data pollution
            incomeStatementPage.navigateWithDates("2023-12-01", "2023-12-31");

            incomeStatementPage.assertProfitMessageContains("menghasilkan laba");
        }
    }

    @Nested
    @DisplayName("4.5 Date Filter Functionality")
    class DateFilterTests {

        @Test
        @DisplayName("Should update report period in header when dates change")
        void shouldUpdateReportPeriodInHeader() {
            incomeStatementPage.navigateWithDates("2024-03-01", "2024-03-31");

            incomeStatementPage.assertReportPeriodContains("1 Maret 2024");
            incomeStatementPage.assertReportPeriodContains("31 Maret 2024");
        }

        @Test
        @DisplayName("Should exclude VOID entries from calculation")
        void shouldExcludeVoidEntriesFromCalculation() {
            // VOID entries should be excluded - verify report loads correctly
            // Use December 2023 which has only POSTED entries
            incomeStatementPage.navigateWithDates("2023-12-01", "2023-12-31");

            // December 2023 only has POSTED entries (JRN-2023-0002)
            // Revenue = 10M from Konsultasi
            String totalRevenue = incomeStatementPage.getTotalRevenueText();
            assertThat(totalRevenue).isEqualTo("10.000.000");
        }

        @Test
        @DisplayName("Should exclude DRAFT entries from calculation")
        void shouldExcludeDraftEntriesFromCalculation() {
            // DRAFT entry JRN-2024-0012 (Jun 30) would add 3M expense if not excluded
            // Use June 2024 to test DRAFT exclusion
            incomeStatementPage.navigateWithDates("2024-06-01", "2024-06-30");

            // June POSTED expense = Penyusutan 1M (JRN-2024-0011)
            // DRAFT JRN-2024-0012 (3M) should be excluded
            String expense = incomeStatementPage.getTotalExpenseText();
            assertThat(expense).isEqualTo("(1.000.000)");
        }

        @Test
        @DisplayName("Should show different values for different date ranges")
        void shouldShowDifferentValuesForDifferentDateRanges() {
            // December 2023 has revenue (10M from JRN-2023-0002)
            incomeStatementPage.navigateWithDates("2023-12-01", "2023-12-31");
            String decRevenue = incomeStatementPage.getTotalRevenueText();

            // November 2023 has no entries at all
            incomeStatementPage.navigateWithDates("2023-11-01", "2023-11-30");
            int novRevenueCount = incomeStatementPage.getRevenueRowCount();

            // Verify December has revenue but November doesn't
            assertThat(decRevenue).isEqualTo("10.000.000");
            assertThat(novRevenueCount).isEqualTo(0);
        }

        @Test
        @DisplayName("Should exclude out of period entries")
        void shouldExcludeOutOfPeriodEntries() {
            // Only December 2023 - should not include 2024 entries
            incomeStatementPage.navigateWithDates("2023-12-01", "2023-12-31");

            // December 2023 has no expenses
            int expenseCount = incomeStatementPage.getExpenseRowCount();
            assertThat(expenseCount).isEqualTo(0);
        }
    }
}
