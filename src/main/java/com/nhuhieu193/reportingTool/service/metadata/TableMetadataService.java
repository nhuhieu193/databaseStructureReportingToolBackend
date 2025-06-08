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

    public Optional<TableMetadataEntity> findById(String tableName) {
        return repository.findById(tableName);
    }

    public TableMetadataEntity save(TableMetadataEntity entity) {
        return repository.save(entity);
    }

    public void deleteById(String tableName) {
        repository.deleteById(tableName);
    }
}
