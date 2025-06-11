package com.nhuhieu193.reportingTool.service.metadata;

import com.nhuhieu193.reportingTool.entity.metadata.TableMetadataEntity;
import com.nhuhieu193.reportingTool.repository.metadata.TableMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TableMetadataService {
    private final TableMetadataRepository repository;

    public TableMetadataService(TableMetadataRepository repository) {
        this.repository = repository;
    }

    public List<TableMetadataEntity> findAll() {
        return repository.findAll();
    }

    // Find by tableName (business logic)
    public Optional<TableMetadataEntity> findByTableName(String tableName) {
        return repository.findByTableName(tableName);
    }

    // Find by ID (primary key)
    public Optional<TableMetadataEntity> findById(Long id) {
        return repository.findById(id);
    }

    public TableMetadataEntity save(TableMetadataEntity entity) {
        return repository.save(entity);
    }

    // Delete by tableName (business logic)
    public void deleteByTableName(String tableName) {
        Optional<TableMetadataEntity> entity = repository.findByTableName(tableName);
        if (entity.isPresent()) {
            repository.delete(entity.get());
        } else {
            throw new RuntimeException("Table not found: " + tableName);
        }
    }

    // Delete by ID (primary key)
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}