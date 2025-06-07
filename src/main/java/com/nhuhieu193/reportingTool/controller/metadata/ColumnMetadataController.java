package com.nhuhieu193.reportingTool.controller.metadata;

import com.nhuhieu193.reportingTool.entity.metadata.ColumnMetadataEntity;
import com.nhuhieu193.reportingTool.service.metadata.ColumnMetadataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ColumnMetadataController {

    private final ColumnMetadataService service;

    public ColumnMetadataController(ColumnMetadataService service) {
        this.service = service;
    }

    @GetMapping("/tables/{tableName}/columns")
    public List<ColumnMetadataEntity> getByTable(@PathVariable String tableName) {
        return service.findByTableName(tableName);
    }

    @PostMapping("/columns")
    public ColumnMetadataEntity create(@RequestBody ColumnMetadataEntity column) {
        return service.save(column);
    }

    @PutMapping("/columns/{id}")
    public ColumnMetadataEntity update(@PathVariable Long id, @RequestBody ColumnMetadataEntity column) {
        column.setId(id);
        return service.save(column);
    }

    @DeleteMapping("/columns/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
