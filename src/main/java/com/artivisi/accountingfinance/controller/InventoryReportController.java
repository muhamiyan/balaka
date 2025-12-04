package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.security.Permission;
import com.artivisi.accountingfinance.service.InventoryReportService;
import com.artivisi.accountingfinance.service.ProductCategoryService;
import com.artivisi.accountingfinance.service.ProductService;
import com.artivisi.accountingfinance.service.ReportExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Controller
@RequestMapping("/inventory/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('" + Permission.INVENTORY_VIEW + "')")
public class InventoryReportController {

    private final InventoryReportService reportService;
    private final ProductCategoryService categoryService;
    private final ProductService productService;
    private final ReportExportService reportExportService;

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @GetMapping
    public String index(Model model) {
        model.addAttribute("currentPage", "inventory-reports");
        return "inventory/reports/index";
    }

    @GetMapping("/stock-balance")
    public String stockBalance(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String search,
            Model model) {

        model.addAttribute("currentPage", "inventory-reports");
        model.addAttribute("reportType", "stock-balance");
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("search", search);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("report", reportService.generateStockBalanceReport(categoryId, search));
        model.addAttribute("asOfDate", LocalDate.now());

        return "inventory/reports/stock-balance";
    }

    @GetMapping("/stock-balance/print")
    public String stockBalancePrint(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String search,
            Model model) {

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("search", search);
        model.addAttribute("report", reportService.generateStockBalanceReport(categoryId, search));
        model.addAttribute("asOfDate", LocalDate.now());

        return "inventory/reports/stock-balance-print";
    }

    @GetMapping("/stock-movement")
    public String stockMovement(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID productId,
            Model model) {

        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        model.addAttribute("currentPage", "inventory-reports");
        model.addAttribute("reportType", "stock-movement");
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("productId", productId);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("products", productService.findAllActive());
        model.addAttribute("report", reportService.generateStockMovementReport(start, end, categoryId, productId));

        return "inventory/reports/stock-movement";
    }

    @GetMapping("/stock-movement/print")
    public String stockMovementPrint(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID productId,
            Model model) {

        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("productId", productId);
        model.addAttribute("report", reportService.generateStockMovementReport(start, end, categoryId, productId));

        return "inventory/reports/stock-movement-print";
    }

    @GetMapping("/valuation")
    public String inventoryValuation(
            @RequestParam(required = false) UUID categoryId,
            Model model) {

        model.addAttribute("currentPage", "inventory-reports");
        model.addAttribute("reportType", "valuation");
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("report", reportService.generateValuationReport(categoryId));
        model.addAttribute("asOfDate", LocalDate.now());

        return "inventory/reports/valuation";
    }

    @GetMapping("/valuation/print")
    public String inventoryValuationPrint(
            @RequestParam(required = false) UUID categoryId,
            Model model) {

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("report", reportService.generateValuationReport(categoryId));
        model.addAttribute("asOfDate", LocalDate.now());

        return "inventory/reports/valuation-print";
    }

    @GetMapping("/profitability")
    public String productProfitability(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID productId,
            Model model) {

        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        model.addAttribute("currentPage", "inventory-reports");
        model.addAttribute("reportType", "profitability");
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("productId", productId);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("products", productService.findAllActive());
        model.addAttribute("report", reportService.generateProfitabilityReport(start, end, categoryId, productId));

        return "inventory/reports/profitability";
    }

    @GetMapping("/profitability/print")
    public String productProfitabilityPrint(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID productId,
            Model model) {

        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("report", reportService.generateProfitabilityReport(start, end, categoryId, productId));

        return "inventory/reports/profitability-print";
    }

    // ==================== EXPORT ENDPOINTS ====================

    // Stock Balance Exports
    @GetMapping("/stock-balance/export/pdf")
    public ResponseEntity<byte[]> exportStockBalanceToPdf(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String search) {
        LocalDate asOfDate = LocalDate.now();
        InventoryReportService.StockBalanceReport report = reportService.generateStockBalanceReport(categoryId, search);
        byte[] pdfBytes = reportExportService.exportStockBalanceToPdf(report, asOfDate);

        String filename = "saldo-stok-" + asOfDate.format(FILE_DATE_FORMAT) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/stock-balance/export/excel")
    public ResponseEntity<byte[]> exportStockBalanceToExcel(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String search) {
        LocalDate asOfDate = LocalDate.now();
        InventoryReportService.StockBalanceReport report = reportService.generateStockBalanceReport(categoryId, search);
        byte[] excelBytes = reportExportService.exportStockBalanceToExcel(report, asOfDate);

        String filename = "saldo-stok-" + asOfDate.format(FILE_DATE_FORMAT) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    // Stock Movement Exports
    @GetMapping("/stock-movement/export/pdf")
    public ResponseEntity<byte[]> exportStockMovementToPdf(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID productId) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        InventoryReportService.StockMovementReport report = reportService.generateStockMovementReport(start, end, categoryId, productId);
        byte[] pdfBytes = reportExportService.exportStockMovementToPdf(report);

        String filename = "mutasi-stok-" + start.format(FILE_DATE_FORMAT) + "-" + end.format(FILE_DATE_FORMAT) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/stock-movement/export/excel")
    public ResponseEntity<byte[]> exportStockMovementToExcel(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID productId) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        InventoryReportService.StockMovementReport report = reportService.generateStockMovementReport(start, end, categoryId, productId);
        byte[] excelBytes = reportExportService.exportStockMovementToExcel(report);

        String filename = "mutasi-stok-" + start.format(FILE_DATE_FORMAT) + "-" + end.format(FILE_DATE_FORMAT) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    // Valuation Exports
    @GetMapping("/valuation/export/pdf")
    public ResponseEntity<byte[]> exportValuationToPdf(
            @RequestParam(required = false) UUID categoryId) {
        LocalDate asOfDate = LocalDate.now();
        InventoryReportService.ValuationReport report = reportService.generateValuationReport(categoryId);
        byte[] pdfBytes = reportExportService.exportValuationToPdf(report, asOfDate);

        String filename = "penilaian-persediaan-" + asOfDate.format(FILE_DATE_FORMAT) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/valuation/export/excel")
    public ResponseEntity<byte[]> exportValuationToExcel(
            @RequestParam(required = false) UUID categoryId) {
        LocalDate asOfDate = LocalDate.now();
        InventoryReportService.ValuationReport report = reportService.generateValuationReport(categoryId);
        byte[] excelBytes = reportExportService.exportValuationToExcel(report, asOfDate);

        String filename = "penilaian-persediaan-" + asOfDate.format(FILE_DATE_FORMAT) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    // Profitability Exports
    @GetMapping("/profitability/export/pdf")
    public ResponseEntity<byte[]> exportProfitabilityToPdf(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID productId) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        InventoryReportService.ProfitabilityReport report = reportService.generateProfitabilityReport(start, end, categoryId, productId);
        byte[] pdfBytes = reportExportService.exportProductProfitabilityToPdf(report);

        String filename = "profitabilitas-produk-" + start.format(FILE_DATE_FORMAT) + "-" + end.format(FILE_DATE_FORMAT) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/profitability/export/excel")
    public ResponseEntity<byte[]> exportProfitabilityToExcel(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID productId) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        InventoryReportService.ProfitabilityReport report = reportService.generateProfitabilityReport(start, end, categoryId, productId);
        byte[] excelBytes = reportExportService.exportProductProfitabilityToExcel(report);

        String filename = "profitabilitas-produk-" + start.format(FILE_DATE_FORMAT) + "-" + end.format(FILE_DATE_FORMAT) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
