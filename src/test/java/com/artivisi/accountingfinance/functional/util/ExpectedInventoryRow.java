package com.artivisi.accountingfinance.functional.util;

import java.math.BigDecimal;

/**
 * Record representing expected inventory levels from CSV test data.
 */
public record ExpectedInventoryRow(
    String productCode,
    String productName,
    BigDecimal expectedQuantity,
    BigDecimal expectedAverageCost,
    String notes
) {}
