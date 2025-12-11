package com.artivisi.accountingfinance.functional.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Utility class to load test data from CSV files.
 */
public final class CsvLoader {

    private CsvLoader() {
        // Utility class
    }

    /**
     * Load production orders from CSV file.
     * @param resourcePath path relative to src/test/resources/testdata/
     * @return list of production order rows
     */
    public static List<ProductionOrderRow> loadProductionOrders(String resourcePath) {
        List<ProductionOrderRow> rows = new ArrayList<>();
        String fullPath = "testdata/" + resourcePath;

        try (var is = CsvLoader.class.getClassLoader().getResourceAsStream(fullPath);
             var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8))) {

            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length >= 7) {
                    rows.add(new ProductionOrderRow(
                        Integer.parseInt(parts[0].trim()),
                        parts[1].trim(),
                        Integer.parseInt(parts[2].trim()),
                        parts[3].trim(),
                        parts[4].trim(),
                        parts[5].trim(),
                        Boolean.parseBoolean(parts[6].trim())
                    ));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CSV: " + fullPath, e);
        }

        return rows;
    }

    /**
     * Load transactions from CSV file.
     * @param resourcePath path relative to src/test/resources/testdata/
     * @return list of transaction rows
     */
    public static List<TransactionRow> loadTransactions(String resourcePath) {
        List<TransactionRow> rows = new ArrayList<>();
        String fullPath = "testdata/" + resourcePath;

        try (var is = CsvLoader.class.getClassLoader().getResourceAsStream(fullPath);
             var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8))) {

            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length >= 12) {
                    rows.add(new TransactionRow(
                        Integer.parseInt(parts[0].trim()),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        parts[4].trim(),
                        parts[5].trim(),
                        parts[6].trim(),
                        parts[7].trim(),
                        Boolean.parseBoolean(parts[8].trim()),
                        parts[9].trim(),  // expectedDebitAccount
                        parts[10].trim(), // expectedCreditAccount
                        parts[11].trim()  // expectedAmount
                    ));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CSV: " + fullPath, e);
        }

        return rows;
    }

    /**
     * Load inventory transactions from CSV file.
     * @param resourcePath path relative to src/test/resources/testdata/
     * @return list of inventory transaction rows
     */
    public static List<InventoryTransactionRow> loadInventoryTransactions(String resourcePath) {
        List<InventoryTransactionRow> rows = new ArrayList<>();
        String fullPath = "testdata/" + resourcePath;

        try (var is = CsvLoader.class.getClassLoader().getResourceAsStream(fullPath);
             var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8))) {

            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length >= 10) {
                    rows.add(new InventoryTransactionRow(
                        Integer.parseInt(parts[0].trim()),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        Integer.parseInt(parts[4].trim()),
                        new BigDecimal(parts[5].trim()),
                        new BigDecimal(parts[6].trim()),
                        parts[7].trim(),
                        parts[8].trim(),
                        Boolean.parseBoolean(parts[9].trim())
                    ));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CSV: " + fullPath, e);
        }

        return rows;
    }

    /**
     * Load expected inventory levels from CSV file.
     * @param resourcePath path relative to src/test/resources/testdata/
     * @return list of expected inventory rows
     */
    public static List<ExpectedInventoryRow> loadExpectedInventory(String resourcePath) {
        List<ExpectedInventoryRow> rows = new ArrayList<>();
        String fullPath = "testdata/" + resourcePath;

        try (var is = CsvLoader.class.getClassLoader().getResourceAsStream(fullPath);
             var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8))) {

            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    rows.add(new ExpectedInventoryRow(
                        parts[0].trim(),
                        parts[1].trim(),
                        new BigDecimal(parts[2].trim()),
                        new BigDecimal(parts[3].trim()),
                        parts[4].trim()
                    ));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load CSV: " + fullPath, e);
        }

        return rows;
    }
}
