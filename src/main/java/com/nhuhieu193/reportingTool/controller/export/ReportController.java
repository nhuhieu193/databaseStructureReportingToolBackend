package com.nhuhieu193.reportingTool.controller.export;

import com.nhuhieu193.reportingTool.service.export.PdfExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {

    private final PdfExportService pdfExportService;

    public ReportController(PdfExportService pdfExportService) {
        this.pdfExportService = pdfExportService;
    }

    @GetMapping("/pdf")
    public void exportPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=metadata-report.pdf");
        pdfExportService.exportToPdf(response.getOutputStream());
    }
}
