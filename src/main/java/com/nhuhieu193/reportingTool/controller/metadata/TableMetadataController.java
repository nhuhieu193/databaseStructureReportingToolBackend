package com.nhuhieu193.reportingTool.controller.metadata;

import com.nhuhieu193.reportingTool.entity.metadata.TableMetadataEntity;
import com.nhuhieu193.reportingTool.service.metadata.TableMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "http://localhost:4200")
public class TableMetadataController {

    private final TableMetadataService service;

    public TableMetadataController(TableMetadataService service) {
        this.service = service;
    }

    @GetMapping
    public List<TableMetadataEntity> getAll() {
        return service.findAll();
    }

    @GetMapping("/{tableName}")
    public ResponseEntity<TableMetadataEntity> getByTableName(@PathVariable String tableName) {
        return service.findByTableName(tableName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TableMetadataEntity> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TableMetadataEntity create(@RequestBody TableMetadataEntity entity) {
        return service.save(entity);
    }

    @PutMapping("/{tableName}")
    public ResponseEntity<TableMetadataEntity> update(@PathVariable String tableName, @RequestBody TableMetadataEntity entity) {
        return service.findByTableName(tableName)
                .map(existingEntity -> {
                    entity.setId(existingEntity.getId()); // Keep the same ID
                    entity.setTableName(tableName);
                    return ResponseEntity.ok(service.save(entity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{tableName}")
    public ResponseEntity<Void> deleteByTableName(@PathVariable String tableName) {
        try {
            service.deleteByTableName(tableName);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}