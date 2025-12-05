package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Security Audit Log Viewer")
class AuditLogViewerTest {

    @Nested
    @DisplayName("Audit Log Access and Display")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class AuditLogAccessTests extends PlaywrightTestBase {

        @BeforeEach
        void setUp() {
            loginAsAdmin();
        }

        private void navigateToAuditLogs() {
            navigateToAuditLogs("");
        }

        private void navigateToAuditLogs(String queryParams) {
            // Use longer timeout for audit logs page (may have many entries from other tests)
            page.navigate(baseUrl() + "/settings/audit-logs" + queryParams,
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);
        }

        @Test
        @DisplayName("Should display audit log page")
        void shouldDisplayAuditLogPage() {
            navigateToAuditLogs();

            assertThat(page.locator("#page-title")).containsText("Security Audit Log");
            assertThat(page.locator("#audit-log-table")).isVisible();
        }

        @Test
        @DisplayName("Should display audit log entries")
        void shouldDisplayAuditLogEntries() {
            // Login generates audit log entries
            navigateToAuditLogs();

            // Should have at least one entry (from login)
            assertThat(page.locator("#audit-log-table")).isVisible();

            // Check for login event (from our login)
            assertThat(page.locator("#audit-log-table")).containsText("LOGIN SUCCESS");
        }

        @Test
        @DisplayName("Should filter by event type")
        void shouldFilterByEventType() {
            navigateToAuditLogs();

            // Select LOGIN_SUCCESS event type
            page.locator("#event-type-filter").selectOption("LOGIN_SUCCESS");
            page.locator("#btn-apply").click();

            // Wait for HTMX to update
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // All visible entries should be LOGIN_SUCCESS
            assertThat(page.locator("#audit-log-table")).containsText("LOGIN SUCCESS");
        }

        @Test
        @DisplayName("Should filter by username")
        void shouldFilterByUsername() {
            navigateToAuditLogs();

            // Filter by admin username
            page.locator("#username-filter").fill("admin");
            page.locator("#btn-apply").click();

            // Wait for HTMX to update
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Results should contain admin
            assertThat(page.locator("#audit-log-table")).containsText("admin");
        }

        @Test
        @DisplayName("Should reset filters")
        void shouldResetFilters() {
            navigateToAuditLogs("?eventType=LOGIN_SUCCESS&username=admin");

            // Click reset button
            page.locator("#btn-reset").click();

            // Wait for page load
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Filters should be cleared
            assertThat(page.locator("#event-type-filter")).hasValue("");
            assertThat(page.locator("#username-filter")).hasValue("");
        }

        @Test
        @DisplayName("Should display event type badges with correct colors")
        void shouldDisplayEventTypeBadges() {
            navigateToAuditLogs();

            // Check that LOGIN_SUCCESS badge exists (green color)
            assertThat(page.locator("#audit-log-table .bg-green-100").first()).isVisible();
        }
    }

    @Nested
    @DisplayName("Audit Log Pagination")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class AuditLogPaginationTests extends PlaywrightTestBase {

        @BeforeEach
        void setUp() {
            loginAsAdmin();
        }

        @Test
        @DisplayName("Should show audit log table even with few entries")
        void shouldShowAuditLogTable() {
            page.navigate(baseUrl() + "/settings/audit-logs",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Check audit log table is displayed (pagination only shows if > 1 page)
            assertThat(page.locator("#audit-log-table")).isVisible();
        }
    }

    @Nested
    @DisplayName("Audit Log Authorization")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class AuditLogAuthorizationTests extends PlaywrightTestBase {

        @Test
        @DisplayName("Should require authentication")
        void shouldRequireAuthentication() {
            // Navigate without login
            page.navigate(baseUrl() + "/settings/audit-logs",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Should redirect to login
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/login.*"));
        }
    }
}
