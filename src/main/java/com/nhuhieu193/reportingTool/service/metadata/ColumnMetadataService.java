package com.nhuhieu193.reportingTool.service.metadata;

import com.nhuhieu193.reportingTool.entity.metadata.ColumnMetadataEntity;
import com.nhuhieu193.reportingTool.repository.metadata.ColumnMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColumnMetadataService {

    private final ColumnMetadataRepository repository;

    public ColumnMetadataService(ColumnMetadataRepository repository) {
        this.repository = repository;
    }

    public List<ColumnMetadataEntity> findByTableName(String tableName) {
        return repository.findByTableName(tableName);
    }

    public ColumnMetadataEntity save(ColumnMetadataEntity column) {
        return repository.save(column);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
