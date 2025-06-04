package com.nhuhieu193.reportingTool.entity.metadata;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "table_metadata")
@Data
public class TableMetadataEntity {
    @Id
    private String tableName;

    private String schemaName;
    private String description;
}
