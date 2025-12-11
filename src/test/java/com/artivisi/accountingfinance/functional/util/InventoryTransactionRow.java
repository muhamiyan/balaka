package com.artivisi.accountingfinance.functional.util;

import java.math.BigDecimal;

/**
 * Record representing an inventory transaction from CSV test data.
 */
public record InventoryTransactionRow(
    int sequence,
    String date,
    String transactionType,
    String productCode,
    int quantity,
    BigDecimal unitCost,
    BigDecimal unitPrice,
    String reference,
    String notes,
    boolean screenshot
) {}
