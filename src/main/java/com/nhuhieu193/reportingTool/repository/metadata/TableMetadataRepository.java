package com.nhuhieu193.reportingTool.repository.metadata;

import com.nhuhieu193.reportingTool.entity.metadata.TableMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TableMetadataRepository extends JpaRepository<TableMetadataEntity, Long> {

    // Custom method to find by tableName
    Optional<TableMetadataEntity> findByTableName(String tableName);

    // Custom method to delete by tableName
    void deleteByTableName(String tableName);

    // Check if table exists by name
    boolean existsByTableName(String tableName);
}