package com.db.dsg.controller;

import com.db.dsg.service.impl.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminExportController {

    private final ExportService exportService;

    @GetMapping("/savings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportAllSavings(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) throws Exception {
        byte[] data;
        String contentType, fileName;

        if ("pdf".equalsIgnoreCase(format)) {
            data = exportService.exportAllSavingsAsPDF(from, to);
            contentType = MediaType.APPLICATION_PDF_VALUE;
            fileName = "all_savings_report.pdf";
        } else {
            data = exportService.exportAllSavingsAsCSV(from, to);
            contentType = "text/csv";
            fileName = "all_savings_report.csv";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    @GetMapping("/repayments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportAllRepayments(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) throws Exception {
        byte[] data;
        String contentType, fileName;

        if ("pdf".equalsIgnoreCase(format)) {
            data = exportService.exportAllRepaymentsAsPDF(from, to, null);
            contentType = MediaType.APPLICATION_PDF_VALUE;
            fileName = "all_loan_repayments.pdf";
        } else {
            data = exportService.exportAllRepaymentsAsCSV(from, to, null);
            contentType = "text/csv";
            fileName = "all_loan_repayments.csv";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }
}
