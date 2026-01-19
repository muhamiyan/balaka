package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ProjectRepository;
import com.artivisi.accountingfinance.repository.ClientRepository;
import com.artivisi.accountingfinance.service.FiscalYearClosingService;
import com.artivisi.accountingfinance.service.ReportService;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for Report Controller API endpoints.
 * Tests REST API responses, print endpoints, and fiscal closing operations.
 */
@DisplayName("Report Controller - API Tests")
@Import(ServiceTestDataInitializer.class)
class ReportApiPersistenceTest extends PlaywrightTestBase {

    @Autowired
    private ReportService reportService;

    @Autowired
    private FiscalYearClosingService fiscalYearClosingService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    // ==================== REST API ENDPOINTS ====================

    @Nested
    @DisplayName("Financial Report APIs")
    class FinancialReportApis {

        @Test
        @DisplayName("Should call trial balance API endpoint")
        void shouldCallTrialBalanceApiEndpoint() {
            String today = LocalDate.now().toString();
            var response = page.request().get(
                baseUrl() + "/reports/api/trial-balance?asOfDate=" + today,
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
            org.assertj.core.api.Assertions.assertThat(response.headers().get("content-type")).contains("application/json");
        }

        @Test
        @DisplayName("Should call income statement API endpoint")
        void shouldCallIncomeStatementApiEndpoint() {
            String startDate = LocalDate.now().withDayOfMonth(1).toString();
            String endDate = LocalDate.now().toString();
            var response = page.request().get(
                baseUrl() + "/reports/api/income-statement?startDate=" + startDate + "&endDate=" + endDate,
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
            org.assertj.core.api.Assertions.assertThat(response.headers().get("content-type")).contains("application/json");
        }

        @Test
        @DisplayName("Should call balance sheet API endpoint")
        void shouldCallBalanceSheetApiEndpoint() {
            String today = LocalDate.now().toString();
            var response = page.request().get(
                baseUrl() + "/reports/api/balance-sheet?asOfDate=" + today,
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
            org.assertj.core.api.Assertions.assertThat(response.headers().get("content-type")).contains("application/json");
        }
    }

    @Nested
    @DisplayName("Tax Report APIs")
    class TaxReportApis {

        @Test
        @DisplayName("Should call PPN summary API endpoint")
        void shouldCallPpnSummaryApiEndpoint() {
            String startDate = LocalDate.now().withDayOfMonth(1).toString();
            String endDate = LocalDate.now().toString();
            var response = page.request().get(
                baseUrl() + "/reports/api/ppn-summary?startDate=" + startDate + "&endDate=" + endDate,
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }

        @Test
        @DisplayName("Should call PPh23 withholding API endpoint")
        void shouldCallPph23WithholdingApiEndpoint() {
            String startDate = LocalDate.now().withDayOfMonth(1).toString();
            String endDate = LocalDate.now().toString();
            var response = page.request().get(
                baseUrl() + "/reports/api/pph23-withholding?startDate=" + startDate + "&endDate=" + endDate,
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }

        @Test
        @DisplayName("Should call tax summary API endpoint")
        void shouldCallTaxSummaryApiEndpoint() {
            String startDate = LocalDate.now().withDayOfMonth(1).toString();
            String endDate = LocalDate.now().toString();
            var response = page.request().get(
                baseUrl() + "/reports/api/tax-summary?startDate=" + startDate + "&endDate=" + endDate,
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }
    }

    @Nested
    @DisplayName("Asset Report APIs")
    class AssetReportApis {

        @Test
        @DisplayName("Should call depreciation report API endpoint")
        void shouldCallDepreciationReportApiEndpoint() {
            int year = LocalDate.now().getYear();
            var response = page.request().get(
                baseUrl() + "/reports/api/depreciation?year=" + year,
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }
    }

    @Nested
    @DisplayName("Profitability Report APIs")
    class ProfitabilityReportApis {

        @Test
        @DisplayName("Should call project profitability API endpoint")
        void shouldCallProjectProfitabilityApiEndpoint() {
            var project = projectRepository.findAll().stream().findFirst();
            if (project.isEmpty()) {
                return;
            }

            String startDate = LocalDate.now().minusMonths(1).toString();
            String endDate = LocalDate.now().toString();
            var response = page.request().get(
                baseUrl() + "/reports/api/project-profitability?projectId=" + project.get().getId()
                    + "&startDate=" + startDate + "&endDate=" + endDate,
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }

        @Test
        @DisplayName("Should call client profitability API endpoint")
        void shouldCallClientProfitabilityApiEndpoint() {
            var client = clientRepository.findAll().stream().findFirst();
            if (client.isEmpty()) {
                return;
            }

            String startDate = LocalDate.now().minusMonths(1).toString();
            String endDate = LocalDate.now().toString();
            var response = page.request().get(
                baseUrl() + "/reports/api/client-profitability?clientId=" + client.get().getId()
                    + "&startDate=" + startDate + "&endDate=" + endDate,
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }

        @Test
        @DisplayName("Should call cost overrun API endpoint")
        void shouldCallCostOverrunApiEndpoint() {
            var project = projectRepository.findAll().stream().findFirst();
            if (project.isEmpty()) {
                return;
            }

            var response = page.request().get(
                baseUrl() + "/reports/api/cost-overrun?projectId=" + project.get().getId(),
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }
    }

    @Nested
    @DisplayName("Fiscal Closing APIs")
    class FiscalClosingApis {

        @Test
        @DisplayName("Should call fiscal closing preview API endpoint")
        void shouldCallFiscalClosingPreviewApiEndpoint() {
            int year = LocalDate.now().getYear();
            var response = page.request().get(
                baseUrl() + "/reports/api/fiscal-closing/preview?year=" + year,
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }
    }

    // ==================== PRINT ENDPOINTS ====================

    @Nested
    @DisplayName("Print Endpoints")
    class PrintEndpoints {

        @Test
        @DisplayName("Should display trial balance print page")
        void shouldDisplayTrialBalancePrintPage() {
            String today = LocalDate.now().toString();
            navigateTo("/reports/trial-balance/print?asOfDate=" + today);
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display balance sheet print page")
        void shouldDisplayBalanceSheetPrintPage() {
            String today = LocalDate.now().toString();
            navigateTo("/reports/balance-sheet/print?asOfDate=" + today);
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display income statement print page")
        void shouldDisplayIncomeStatementPrintPage() {
            String startDate = LocalDate.now().withDayOfMonth(1).toString();
            String endDate = LocalDate.now().toString();
            navigateTo("/reports/income-statement/print?startDate=" + startDate + "&endDate=" + endDate);
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display cash flow print page")
        void shouldDisplayCashFlowPrintPage() {
            String startDate = LocalDate.now().withDayOfMonth(1).toString();
            String endDate = LocalDate.now().toString();
            navigateTo("/reports/cash-flow/print?startDate=" + startDate + "&endDate=" + endDate);
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display PPN summary print page")
        void shouldDisplayPpnSummaryPrintPage() {
            int year = LocalDate.now().getYear();
            int month = LocalDate.now().getMonthValue();
            navigateTo("/reports/ppn-summary/print?year=" + year + "&month=" + month);
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display PPh23 withholding print page")
        void shouldDisplayPph23WithholdingPrintPage() {
            int year = LocalDate.now().getYear();
            int month = LocalDate.now().getMonthValue();
            navigateTo("/reports/pph23-withholding/print?year=" + year + "&month=" + month);
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display depreciation print page")
        void shouldDisplayDepreciationPrintPage() {
            int year = LocalDate.now().getYear();
            int month = LocalDate.now().getMonthValue();
            navigateTo("/reports/depreciation/print?year=" + year + "&month=" + month);
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== SERVICE LAYER TESTS ====================

    @Nested
    @DisplayName("Report Service Tests")
    class ReportServiceTests {

        @Test
        @DisplayName("Should generate trial balance report")
        void shouldGenerateTrialBalanceReport() {
            var asOfDate = LocalDate.now();
            var report = reportService.generateTrialBalance(asOfDate);

            org.assertj.core.api.Assertions.assertThat(report).isNotNull();
        }

        @Test
        @DisplayName("Should generate income statement report")
        void shouldGenerateIncomeStatementReport() {
            var startDate = LocalDate.now().withDayOfMonth(1);
            var endDate = LocalDate.now();
            var report = reportService.generateIncomeStatement(startDate, endDate);

            org.assertj.core.api.Assertions.assertThat(report).isNotNull();
        }

        @Test
        @DisplayName("Should generate balance sheet report")
        void shouldGenerateBalanceSheetReport() {
            var asOfDate = LocalDate.now();
            var report = reportService.generateBalanceSheet(asOfDate);

            org.assertj.core.api.Assertions.assertThat(report).isNotNull();
        }

        @Test
        @DisplayName("Should generate cash flow statement")
        void shouldGenerateCashFlowStatement() {
            var startDate = LocalDate.now().withDayOfMonth(1);
            var endDate = LocalDate.now();
            var report = reportService.generateCashFlowStatement(startDate, endDate);

            org.assertj.core.api.Assertions.assertThat(report).isNotNull();
        }
    }

    // ==================== FISCAL CLOSING SERVICE TESTS ====================

    @Nested
    @DisplayName("Fiscal Closing Service Tests")
    class FiscalClosingServiceTests {

        @Test
        @DisplayName("Should preview fiscal closing for current year")
        void shouldPreviewFiscalClosingForCurrentYear() {
            int year = LocalDate.now().getYear();
            var preview = fiscalYearClosingService.previewClosing(year);

            org.assertj.core.api.Assertions.assertThat(preview).isNotNull();
            org.assertj.core.api.Assertions.assertThat(preview.year()).isEqualTo(year);
            org.assertj.core.api.Assertions.assertThat(preview.totalRevenue()).isNotNull();
            org.assertj.core.api.Assertions.assertThat(preview.totalExpense()).isNotNull();
            org.assertj.core.api.Assertions.assertThat(preview.netIncome()).isNotNull();
        }

        @Test
        @DisplayName("Should preview fiscal closing for far past year with zero activity")
        void shouldPreviewFiscalClosingForFarPastYear() {
            var preview = fiscalYearClosingService.previewClosing(1990);

            org.assertj.core.api.Assertions.assertThat(preview).isNotNull();
            org.assertj.core.api.Assertions.assertThat(preview.totalRevenue()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
            org.assertj.core.api.Assertions.assertThat(preview.totalExpense()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
            org.assertj.core.api.Assertions.assertThat(preview.netIncome()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
            org.assertj.core.api.Assertions.assertThat(preview.entries()).isEmpty();
        }

        @Test
        @DisplayName("Should check has closing entries for non-existent year")
        void shouldCheckHasClosingEntriesForNonExistentYear() {
            boolean hasClosing = fiscalYearClosingService.hasClosingEntries(1990);
            org.assertj.core.api.Assertions.assertThat(hasClosing).isFalse();
        }

        @Test
        @DisplayName("Should get empty closing entries for non-existent year")
        void shouldGetEmptyClosingEntriesForNonExistentYear() {
            var entries = fiscalYearClosingService.getClosingEntries(1990);
            org.assertj.core.api.Assertions.assertThat(entries).isEmpty();
        }
    }
}
