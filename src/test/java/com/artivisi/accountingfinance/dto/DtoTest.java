package com.artivisi.accountingfinance.dto;

import com.artivisi.accountingfinance.enums.VoidReason;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void formulaContextBuilder_shouldBuildWithVariables() {
        FormulaContext context = FormulaContext.builder(BigDecimal.TEN)
                .variable("tax", BigDecimal.valueOf(11))
                .variable("discount", BigDecimal.valueOf(5))
                .build();

        assertThat(context.amount()).isEqualTo(BigDecimal.TEN);
        assertThat(context.get("tax")).isEqualTo(BigDecimal.valueOf(11));
        assertThat(context.get("discount")).isEqualTo(BigDecimal.valueOf(5));
    }

    @Test
    void formulaContextBuilder_shouldBuildWithEmptyVariables() {
        FormulaContext context = FormulaContext.builder(BigDecimal.ONE).build();

        assertThat(context.amount()).isEqualTo(BigDecimal.ONE);
        assertThat(context.variables()).isEmpty();
    }

    @Test
    void formulaPreviewResponse_shouldCreateErrorResponse() {
        List<String> errors = List.of("Invalid formula", "Missing variable");
        FormulaPreviewResponse response = FormulaPreviewResponse.error(errors);

        assertThat(response.valid()).isFalse();
        assertThat(response.result()).isNull();
        assertThat(response.formattedResult()).isNull();
        assertThat(response.errors()).containsExactly("Invalid formula", "Missing variable");
    }

    @Test
    void executeTemplateDto_shouldReturnSafeVariables() {
        ExecuteTemplateDto dto = new ExecuteTemplateDto(
                LocalDate.now(), BigDecimal.TEN, "Test", null, null);

        assertThat(dto.safeVariables()).isNotNull().isEmpty();
    }

    @Test
    void executeTemplateDto_shouldReturnVariablesWhenProvided() {
        Map<String, BigDecimal> vars = Map.of("test", BigDecimal.ONE);
        ExecuteTemplateDto dto = new ExecuteTemplateDto(
                LocalDate.now(), BigDecimal.TEN, "Test", vars, null);

        assertThat(dto.safeVariables()).isEqualTo(vars);
    }

    @Test
    void executeTemplateDto_shouldReturnSafeAccountMappings() {
        ExecuteTemplateDto dto = new ExecuteTemplateDto(
                LocalDate.now(), BigDecimal.TEN, "Test", null, null);

        assertThat(dto.safeAccountMappings()).isNotNull().isEmpty();
    }

    @Test
    void executeTemplateDto_shouldReturnAccountMappingsWhenProvided() {
        Map<String, String> mappings = Map.of("1", UUID.randomUUID().toString());
        ExecuteTemplateDto dto = new ExecuteTemplateDto(
                LocalDate.now(), BigDecimal.TEN, "Test", null, mappings);

        assertThat(dto.safeAccountMappings()).isEqualTo(mappings);
    }

    @Test
    void executeTemplateDto_shouldGetAccountIdForLine() {
        UUID accountId = UUID.randomUUID();
        Map<String, String> mappings = Map.of("1", accountId.toString());
        ExecuteTemplateDto dto = new ExecuteTemplateDto(
                LocalDate.now(), BigDecimal.TEN, "Test", null, mappings);

        assertThat(dto.getAccountIdForLine(1)).isEqualTo(accountId);
    }

    @Test
    void executeTemplateDto_shouldReturnNullForUnmappedLine() {
        ExecuteTemplateDto dto = new ExecuteTemplateDto(
                LocalDate.now(), BigDecimal.TEN, "Test", null, null);

        assertThat(dto.getAccountIdForLine(99)).isNull();
    }

    @Test
    void executeTemplateDto_shouldReturnNullForBlankMapping() {
        Map<String, String> mappings = Map.of("1", "");
        ExecuteTemplateDto dto = new ExecuteTemplateDto(
                LocalDate.now(), BigDecimal.TEN, "Test", null, mappings);

        assertThat(dto.getAccountIdForLine(1)).isNull();
    }

    @Test
    void accountOptionDto_shouldStoreValues() {
        AccountOptionDto dto = new AccountOptionDto("id1", "1.1.01", "Kas");

        assertThat(dto.id()).isEqualTo("id1");
        assertThat(dto.accountCode()).isEqualTo("1.1.01");
        assertThat(dto.accountName()).isEqualTo("Kas");
    }

    @Test
    void voidTransactionDto_shouldStoreValues() {
        VoidTransactionDto dto = new VoidTransactionDto(VoidReason.DUPLICATE, "Test notes");

        assertThat(dto.reason()).isEqualTo(VoidReason.DUPLICATE);
        assertThat(dto.notes()).isEqualTo("Test notes");
    }

    @Test
    void voidTransactionDto_shouldAllowNullNotes() {
        VoidTransactionDto dto = new VoidTransactionDto(VoidReason.INPUT_ERROR, null);

        assertThat(dto.reason()).isEqualTo(VoidReason.INPUT_ERROR);
        assertThat(dto.notes()).isNull();
    }
}
