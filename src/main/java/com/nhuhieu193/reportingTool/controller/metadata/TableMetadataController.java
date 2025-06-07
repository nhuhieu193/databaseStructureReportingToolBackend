package com.nhuhieu193.reportingTool.controller.metadata;

import com.nhuhieu193.reportingTool.entity.metadata.TableMetadataEntity;
import com.nhuhieu193.reportingTool.service.TableMetadataService;
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
    public TableMetadataEntity getById(@PathVariable String tableName) {
        return service.findById(tableName).orElseThrow();
    }

    @PostMapping
    public TableMetadataEntity create(@RequestBody TableMetadataEntity entity) {
        return service.save(entity);
    }

    @PutMapping("/{tableName}")
    public TableMetadataEntity update(@PathVariable String tableName, @RequestBody TableMetadataEntity entity) {
        entity.setTableName(tableName);
        return service.save(entity);
    }

    @DeleteMapping("/{tableName}")
    public void delete(@PathVariable String tableName) {
        service.deleteById(tableName);
    }
}
