package com.nhuhieu193.reportingTool.service.metadata;

import com.nhuhieu193.reportingTool.entity.metadata.ColumnMetadataEntity;
import com.nhuhieu193.reportingTool.entity.metadata.TableMetadataEntity;
import com.nhuhieu193.reportingTool.repository.metadata.ColumnMetadataRepository;
import com.nhuhieu193.reportingTool.repository.metadata.TableMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColumnMetadataService {

    private final ColumnMetadataRepository repository;
    private final TableMetadataRepository tableRepository;

    public ColumnMetadataService(ColumnMetadataRepository repository, TableMetadataRepository tableRepository) {
        this.repository = repository;
        this.tableRepository = tableRepository;
    }

    public List<ColumnMetadataEntity> findByTableName(String tableName) {
        return repository.findByTable_TableName(tableName);
    }

    public ColumnMetadataEntity saveWithTableName(String tableName, ColumnMetadataEntity column) {
        TableMetadataEntity table = tableRepository.findByTableName(tableName)
                .orElseThrow(() -> new RuntimeException("Table not found with name: " + tableName));

        column.setTable(table);
        return repository.save(column);
    }

    public ColumnMetadataEntity update(Long id, ColumnMetadataEntity column) {
        // Tìm column hiện tại
        Optional<ColumnMetadataEntity> existingOpt = repository.findById(id);
        if (existingOpt.isPresent()) {
            ColumnMetadataEntity existing = existingOpt.get();

            // Update các field
            existing.setColumnName(column.getColumnName());
            existing.setDataType(column.getDataType());
            existing.setColumnSize(column.getColumnSize());
            existing.setNullable(column.getNullable());

            // Nếu có table info mới, update table relationship
            if (column.getTable() != null && column.getTable().getTableName() != null) {
                Optional<TableMetadataEntity> tableOpt = tableRepository.findByTableName(column.getTable().getTableName());
                if (tableOpt.isPresent()) {
                    existing.setTable(tableOpt.get());
                }
            }

            return repository.save(existing);
        } else {
            throw new RuntimeException("Column not found with id: " + id);
        }
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}