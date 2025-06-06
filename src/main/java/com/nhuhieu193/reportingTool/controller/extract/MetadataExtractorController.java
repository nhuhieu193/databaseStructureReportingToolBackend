package com.nhuhieu193.reportingTool.controller.extract;

import com.nhuhieu193.reportingTool.model.TableMetadata;
import com.nhuhieu193.reportingTool.service.extract.MetadataExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/metadata")
public class MetadataExtractorController {

    @Autowired
    private MetadataExtractorService extractorService;

    @GetMapping
    public List<TableMetadata> getMetadata() throws SQLException {
        return extractorService.extractDatabaseStructure();
    }
}
