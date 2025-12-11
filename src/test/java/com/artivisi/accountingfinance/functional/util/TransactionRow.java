package com.artivisi.accountingfinance.functional.util;

/**
 * Record representing a transaction from CSV test data.
 * Includes expected journal entry data for verification.
 */
public record TransactionRow(
    int sequence,
    String date,
    String templateName,
    String inputs,
    String description,
    String reference,
    String project,
    String notes,
    boolean screenshot,
    String expectedDebitAccount,
    String expectedCreditAccount,
    String expectedAmount
) {}
