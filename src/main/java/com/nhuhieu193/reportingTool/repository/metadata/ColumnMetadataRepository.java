package com.nhuhieu193.reportingTool.repository.metadata;

import com.nhuhieu193.reportingTool.entity.metadata.ColumnMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColumnMetadataRepository extends JpaRepository<ColumnMetadataEntity, Long> {
    List<ColumnMetadataEntity> findByTableName(String tableName);
}
