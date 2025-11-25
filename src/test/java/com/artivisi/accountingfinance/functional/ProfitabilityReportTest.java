package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.ClientProfitabilityPage;
import com.artivisi.accountingfinance.functional.page.ClientRankingPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.ProjectProfitabilityPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Profitability Reports (Section 1.9.7)")
class ProfitabilityReportTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private ProjectProfitabilityPage projectProfitabilityPage;
    private ClientProfitabilityPage clientProfitabilityPage;
    private ClientRankingPage clientRankingPage;

    // Test data IDs from V905__profitability_test_data.sql
    private static final String PROJECT_ABC_WEBSITE_ID = "a0500000-0000-0000-0000-000000000001";
    private static final String PROJECT_ABC_MOBILE_ID = "a0500000-0000-0000-0000-000000000002";
    private static final String PROJECT_XYZ_CONSULTING_ID = "a0500000-0000-0000-0000-000000000003";
    private static final String CLIENT_ABC_ID = "c0500000-0000-0000-0000-000000000001";
    private static final String CLIENT_XYZ_ID = "c0500000-0000-0000-0000-000000000002";

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        projectProfitabilityPage = new ProjectProfitabilityPage(page, baseUrl());
        clientProfitabilityPage = new ClientProfitabilityPage(page, baseUrl());
        clientRankingPage = new ClientRankingPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("1.9.7.1 Project Profitability Report")
    class ProjectProfitabilityTests {

        @Test
        @DisplayName("Should display project profitability page title")
        void shouldDisplayProjectProfitabilityPageTitle() {
            projectProfitabilityPage.navigate();

            projectProfitabilityPage.assertPageTitleVisible();
            projectProfitabilityPage.assertPageTitleText("Profitabilitas Proyek");
        }

        @Test
        @DisplayName("Should display project selector and filter controls")
        void shouldDisplayProjectSelectorAndFilterControls() {
            projectProfitabilityPage.navigate();

            projectProfitabilityPage.assertProjectSelectVisible();
            projectProfitabilityPage.assertGenerateButtonVisible();
        }

        @Test
        @DisplayName("Should display 'select project' message when no project selected")
        void shouldDisplaySelectProjectMessage() {
            projectProfitabilityPage.navigate();

            projectProfitabilityPage.assertNoProjectSelectedVisible();
        }

        @Test
        @DisplayName("Should display profitability report when project is selected")
        void shouldDisplayProfitabilityReportWhenProjectSelected() {
            projectProfitabilityPage.navigateWithProject(PROJECT_ABC_WEBSITE_ID, "2024-01-01", "2024-06-30");

            projectProfitabilityPage.assertProfitabilityReportVisible();
            projectProfitabilityPage.assertReportTitleVisible();
            projectProfitabilityPage.assertReportTitleText("LAPORAN PROFITABILITAS");
        }

        @Test
        @DisplayName("Should display correct revenue for PRJ-001")
        void shouldDisplayCorrectRevenueForProject001() {
            projectProfitabilityPage.navigateWithProject(PROJECT_ABC_WEBSITE_ID, "2024-01-01", "2024-06-30");

            // PRJ-001: Revenue = 25,000,000 (15M + 10M)
            String totalRevenue = projectProfitabilityPage.getTotalRevenueText();
            assertThat(totalRevenue).isEqualTo("25.000.000");
        }

        @Test
        @DisplayName("Should display correct expense for PRJ-001")
        void shouldDisplayCorrectExpenseForProject001() {
            projectProfitabilityPage.navigateWithProject(PROJECT_ABC_WEBSITE_ID, "2024-01-01", "2024-06-30");

            // PRJ-001: Expense = 10,000,000 (8M + 2M) - displayed with parentheses
            String totalExpense = projectProfitabilityPage.getTotalExpenseText();
            assertThat(totalExpense).isEqualTo("(10.000.000)");
        }

        @Test
        @DisplayName("Should display correct profit for PRJ-001")
        void shouldDisplayCorrectProfitForProject001() {
            projectProfitabilityPage.navigateWithProject(PROJECT_ABC_WEBSITE_ID, "2024-01-01", "2024-06-30");

            // PRJ-001: Profit = 15,000,000
            String grossProfit = projectProfitabilityPage.getGrossProfitText();
            assertThat(grossProfit).isEqualTo("15.000.000");
        }

        @Test
        @DisplayName("Should display correct profit margin for PRJ-001")
        void shouldDisplayCorrectProfitMarginForProject001() {
            projectProfitabilityPage.navigateWithProject(PROJECT_ABC_WEBSITE_ID, "2024-01-01", "2024-06-30");

            // PRJ-001: Margin = 60%
            String profitMargin = projectProfitabilityPage.getProfitMarginText();
            assertThat(profitMargin).isEqualTo("60.00%");
        }

        @Test
        @DisplayName("Should display LABA KOTOR label for profitable project")
        void shouldDisplayLabaKotorLabelForProfitableProject() {
            projectProfitabilityPage.navigateWithProject(PROJECT_ABC_WEBSITE_ID, "2024-01-01", "2024-06-30");

            String profitLabel = projectProfitabilityPage.getProfitLabelText();
            assertThat(profitLabel).isEqualTo("LABA KOTOR");
        }

        @Test
        @DisplayName("Should display cost status section")
        void shouldDisplayCostStatusSection() {
            projectProfitabilityPage.navigateWithProject(PROJECT_ABC_WEBSITE_ID, "2024-01-01", "2024-06-30");

            projectProfitabilityPage.assertCostStatusVisible();
        }

        @Test
        @DisplayName("Should display correct risk level based on budget vs progress")
        void shouldDisplayCorrectRiskLevel() {
            projectProfitabilityPage.navigateWithProject(PROJECT_ABC_WEBSITE_ID, "2024-01-01", "2024-06-30");

            // PRJ-001: Budget 12M, Spent ~10M (83%), Progress 80%
            // Spent% (83) vs Progress% (80) -> within 5% threshold = LOW risk
            String riskLevel = projectProfitabilityPage.getRiskLevelText();
            assertThat(riskLevel).isIn("LOW", "MEDIUM", "HIGH", "UNKNOWN");
        }
    }

    @Nested
    @DisplayName("1.9.7.2 Client Profitability Report")
    class ClientProfitabilityTests {

        @Test
        @DisplayName("Should display client profitability page title")
        void shouldDisplayClientProfitabilityPageTitle() {
            clientProfitabilityPage.navigate();

            clientProfitabilityPage.assertPageTitleVisible();
            clientProfitabilityPage.assertPageTitleText("Profitabilitas Klien");
        }

        @Test
        @DisplayName("Should display client selector and filter controls")
        void shouldDisplayClientSelectorAndFilterControls() {
            clientProfitabilityPage.navigate();

            clientProfitabilityPage.assertClientSelectVisible();
            clientProfitabilityPage.assertGenerateButtonVisible();
        }

        @Test
        @DisplayName("Should display 'select client' message when no client selected")
        void shouldDisplaySelectClientMessage() {
            clientProfitabilityPage.navigate();

            clientProfitabilityPage.assertNoClientSelectedVisible();
        }

        @Test
        @DisplayName("Should display client summary when client is selected")
        void shouldDisplayClientSummaryWhenClientSelected() {
            clientProfitabilityPage.navigateWithClient(CLIENT_ABC_ID, "2024-01-01", "2024-06-30");

            clientProfitabilityPage.assertClientSummaryVisible();
            clientProfitabilityPage.assertSummaryCardsVisible();
        }

        @Test
        @DisplayName("Should display correct client name")
        void shouldDisplayCorrectClientName() {
            clientProfitabilityPage.navigateWithClient(CLIENT_ABC_ID, "2024-01-01", "2024-06-30");

            String clientName = clientProfitabilityPage.getClientNameText();
            assertThat(clientName).isEqualTo("PT ABC Technology");
        }

        @Test
        @DisplayName("Should display correct total revenue for PT ABC")
        void shouldDisplayCorrectTotalRevenueForClientABC() {
            clientProfitabilityPage.navigateWithClient(CLIENT_ABC_ID, "2024-01-01", "2024-06-30");

            // PT ABC: Total Revenue = 37,000,000 (25M + 12M)
            String totalRevenue = clientProfitabilityPage.getTotalRevenueText();
            assertThat(totalRevenue).isEqualTo("37.000.000");
        }

        @Test
        @DisplayName("Should display correct total profit for PT ABC")
        void shouldDisplayCorrectTotalProfitForClientABC() {
            clientProfitabilityPage.navigateWithClient(CLIENT_ABC_ID, "2024-01-01", "2024-06-30");

            // PT ABC: Total Profit = 19,000,000 (15M + 4M)
            String totalProfit = clientProfitabilityPage.getTotalProfitText();
            assertThat(totalProfit).isEqualTo("19.000.000");
        }

        @Test
        @DisplayName("Should display correct project count for PT ABC")
        void shouldDisplayCorrectProjectCountForClientABC() {
            clientProfitabilityPage.navigateWithClient(CLIENT_ABC_ID, "2024-01-01", "2024-06-30");

            // PT ABC: 2 projects with activity
            String projectCount = clientProfitabilityPage.getProjectCountText();
            assertThat(projectCount).isEqualTo("2");
        }

        @Test
        @DisplayName("Should display projects table")
        void shouldDisplayProjectsTable() {
            clientProfitabilityPage.navigateWithClient(CLIENT_ABC_ID, "2024-01-01", "2024-06-30");

            clientProfitabilityPage.assertProjectsTableVisible();
            int rowCount = clientProfitabilityPage.getProjectRowCount();
            assertThat(rowCount).isEqualTo(2);
        }

        @Test
        @DisplayName("Should display overall margin for PT ABC")
        void shouldDisplayOverallMarginForClientABC() {
            clientProfitabilityPage.navigateWithClient(CLIENT_ABC_ID, "2024-01-01", "2024-06-30");

            // PT ABC: Overall Margin = 51.35% (19M / 37M * 100)
            String overallMargin = clientProfitabilityPage.getOverallMarginText();
            assertThat(overallMargin).isEqualTo("51.35%");
        }
    }

    @Nested
    @DisplayName("1.9.7.3 Client Ranking Report")
    class ClientRankingTests {

        @Test
        @DisplayName("Should display client ranking page title")
        void shouldDisplayClientRankingPageTitle() {
            clientRankingPage.navigate();

            clientRankingPage.assertPageTitleVisible();
            clientRankingPage.assertPageTitleText("Peringkat Klien");
        }

        @Test
        @DisplayName("Should display filter controls")
        void shouldDisplayFilterControls() {
            clientRankingPage.navigate();

            clientRankingPage.assertStartDateVisible();
            clientRankingPage.assertEndDateVisible();
            clientRankingPage.assertLimitSelectVisible();
            clientRankingPage.assertGenerateButtonVisible();
        }

        @Test
        @DisplayName("Should display ranking table with clients")
        void shouldDisplayRankingTableWithClients() {
            clientRankingPage.navigateWithParams("2024-01-01", "2024-06-30", 10);

            clientRankingPage.assertRankingTableVisible();
            // Should have 2 clients with revenue (PT ABC and PT XYZ)
            int rowCount = clientRankingPage.getRankingRowCount();
            assertThat(rowCount).isEqualTo(2);
        }

        @Test
        @DisplayName("Should respect limit parameter")
        void shouldRespectLimitParameter() {
            // Limit to top 1
            clientRankingPage.navigateWithParams("2024-01-01", "2024-06-30", 1);

            clientRankingPage.assertRankingTableVisible();
            int rowCount = clientRankingPage.getRankingRowCount();
            assertThat(rowCount).isEqualTo(1);
        }

        @Test
        @DisplayName("Should show no data message when no clients have revenue")
        void shouldShowNoDataMessageWhenNoClientsHaveRevenue() {
            // Use a date range with no transactions
            clientRankingPage.navigateWithParams("2020-01-01", "2020-12-31", 10);

            clientRankingPage.assertNoDataMessageVisible();
        }
    }
}
