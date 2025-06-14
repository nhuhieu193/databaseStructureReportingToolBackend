package com.nhuhieu193.reportingTool.controller.metadata;

import com.nhuhieu193.reportingTool.entity.metadata.ColumnMetadataEntity;
import com.nhuhieu193.reportingTool.repository.metadata.TableMetadataRepository;
import com.nhuhieu193.reportingTool.service.metadata.ColumnMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ColumnMetadataController {

    private final ColumnMetadataService service;
    private final TableMetadataRepository tableRepository;

    public ColumnMetadataController(ColumnMetadataService service, TableMetadataRepository tableRepository) {
        this.service = service;
        this.tableRepository = tableRepository; // ✅ Inject dependency
    }

    @GetMapping("/tables/{tableName}/columns")
    public List<ColumnMetadataEntity> getByTable(@PathVariable String tableName) {
        return service.findByTableName(tableName);
    }

    @PostMapping("/table-name/{tableName}")
    public ResponseEntity<ColumnMetadataEntity> addColumnByTableName(
            @PathVariable String tableName,
            @RequestBody ColumnMetadataEntity column) {

        ColumnMetadataEntity savedColumn = this.service.saveWithTableName(tableName, column);
        return ResponseEntity.ok(savedColumn);
    }

    @PutMapping("/columns/{id}")
    public ResponseEntity<ColumnMetadataEntity> update(@PathVariable Long id, @RequestBody ColumnMetadataEntity column) {
        try {
            ColumnMetadataEntity updated = service.update(id, column);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/columns/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}