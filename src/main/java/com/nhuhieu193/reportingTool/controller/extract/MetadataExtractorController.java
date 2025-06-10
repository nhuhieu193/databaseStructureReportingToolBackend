package com.nhuhieu193.reportingTool.controller.extract;

import com.nhuhieu193.reportingTool.service.extract.MetadataExtractorService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/metadata")
public class MetadataExtractorController {

    private final MetadataExtractorService metadataExtractorService;

    public MetadataExtractorController(MetadataExtractorService metadataExtractorService) {
        this.metadataExtractorService = metadataExtractorService;
    }

    @PostMapping("/sync")
    public void syncMetadata(HttpServletResponse response) {
        try {
            metadataExtractorService.extractAndSyncMetadata();
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
}
