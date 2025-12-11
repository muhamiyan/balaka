package com.artivisi.accountingfinance.functional.util;

/**
 * Record representing a production order from CSV test data.
 */
public record ProductionOrderRow(
    int sequence,
    String bomCode,
    int quantity,
    String orderDate,
    String plannedCompletionDate,
    String notes,
    boolean screenshot
) {}
