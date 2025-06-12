package com.nhuhieu193.reportingTool.controller.export;

import com.nhuhieu193.reportingTool.service.export.PdfExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PdfExportController {

    private final PdfExportService pdfExportService;

    public PdfExportController(PdfExportService pdfExportService) {
        this.pdfExportService = pdfExportService;
    }

    @GetMapping("/api/metadata/export/pdf")
    public ResponseEntity<byte[]> exportPdf() {
        byte[] pdf = pdfExportService.exportReportPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=database_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
