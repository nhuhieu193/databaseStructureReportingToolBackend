package com.nhuhieu193.reportingTool.repository.metadata;

import com.nhuhieu193.reportingTool.entity.metadata.TableMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableMetadataRepository extends JpaRepository<TableMetadataEntity, String> {
}
