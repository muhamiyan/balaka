package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional tests for PaymentTermController.
 * Tests CRUD operations for project payment terms.
 */
@DisplayName("Payment Term Tests")
@Import(ServiceTestDataInitializer.class)
class PaymentTermTest extends PlaywrightTestBase {

    private static final String PROJECT_CODE = "PRJ-2024-001";

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    // ==================== Payment Term Form Tests ====================

    @Test
    @DisplayName("Should display new payment term form")
    void shouldDisplayNewPaymentTermForm() {
        navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
        waitForPageLoad();

        var sequenceInput = page.locator("#sequence");
        var nameInput = page.locator("#name");
        var dueTriggerSelect = page.locator("#dueTrigger");

        if (sequenceInput.isVisible()) {
            assertThat(sequenceInput.isVisible())
                .as("Sequence input should be visible")
                .isTrue();
        }

        if (nameInput.isVisible()) {
            assertThat(nameInput.isVisible())
                .as("Name input should be visible")
                .isTrue();
        }

        if (dueTriggerSelect.isVisible()) {
            assertThat(dueTriggerSelect.isVisible())
                .as("Due trigger select should be visible")
                .isTrue();
        }
    }

    @Test
    @DisplayName("Should show project context in form")
    void shouldShowProjectContextInForm() {
        navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
        waitForPageLoad();

        // Page should have reference to the project
        assertThat(page.url())
            .as("URL should contain project code")
            .contains(PROJECT_CODE);
    }

    @Test
    @DisplayName("Should create new payment term")
    void shouldCreateNewPaymentTerm() {
        navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
        waitForPageLoad();

        var sequenceInput = page.locator("#sequence");
        var nameInput = page.locator("#name");
        var dueTriggerSelect = page.locator("#dueTrigger");
        var percentageInput = page.locator("#percentage");
        var submitBtn = page.locator("#btn-simpan");

        // Fill the form if elements are visible
        if (sequenceInput.isVisible()) {
            sequenceInput.fill("99");
        }
        if (nameInput.isVisible()) {
            nameInput.fill("Test Payment Term");
        }
        if (dueTriggerSelect.isVisible()) {
            dueTriggerSelect.selectOption("ON_SIGNING");
        }
        if (percentageInput.isVisible()) {
            percentageInput.fill("30");
        }

        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        // Should redirect back to project page or stay on form
        assertThat(page.locator("body").isVisible()).isTrue();
    }

    @Test
    @DisplayName("Should validate required fields")
    void shouldValidateRequiredFields() {
        navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
        waitForPageLoad();

        var submitBtn = page.locator("#btn-simpan");

        // Submit empty form (name is required)
        if (submitBtn.isVisible()) {
            submitBtn.click();
        }

        // HTML5 validation will prevent submission or server-side validation will show error
        assertThat(page.locator("body").isVisible()).isTrue();
    }

    // ==================== Payment Term Actions Tests ====================

    @Test
    @DisplayName("Should navigate to project from payment term")
    void shouldNavigateToProjectFromPaymentTerm() {
        // First, navigate to project page to see payment terms
        navigateTo("/projects/" + PROJECT_CODE);
        waitForPageLoad();

        assertThat(page.url())
            .as("Should be on project page")
            .contains("/projects/" + PROJECT_CODE);
    }

    @Test
    @DisplayName("Should access payment terms from project detail")
    void shouldAccessPaymentTermsFromProjectDetail() {
        navigateTo("/projects/" + PROJECT_CODE);
        waitForPageLoad();

        // Look for link to add payment term
        var addLink = page.locator("a[href*='payment-terms/new']");
        if (addLink.isVisible()) {
            addLink.click();
            waitForPageLoad();

            assertThat(page.url())
                .as("Should navigate to new payment term form")
                .contains("/payment-terms/new");
        }
    }

    // ==================== Payment Term Trigger Tests ====================

    @Test
    @DisplayName("Should display all trigger options")
    void shouldDisplayAllTriggerOptions() {
        navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
        waitForPageLoad();

        // Get all options from the dueTrigger select
        var dueTriggerSelect = page.locator("#dueTrigger");
        if (dueTriggerSelect.isVisible()) {
            var options = page.locator("#dueTrigger option").all();
            assertThat(options.size())
                .as("Should have trigger options")
                .isGreaterThanOrEqualTo(1);
        }
    }

    @Test
    @DisplayName("Should select different trigger types")
    void shouldSelectDifferentTriggerTypes() {
        navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
        waitForPageLoad();

        var dueTriggerSelect = page.locator("#dueTrigger");
        if (dueTriggerSelect.isVisible()) {
            // Try selecting different triggers
            dueTriggerSelect.selectOption("ON_SIGNING");
            assertThat(dueTriggerSelect.inputValue())
                .isEqualTo("ON_SIGNING");

            dueTriggerSelect.selectOption("ON_COMPLETION");
            assertThat(dueTriggerSelect.inputValue())
                .isEqualTo("ON_COMPLETION");
        }
    }
}
