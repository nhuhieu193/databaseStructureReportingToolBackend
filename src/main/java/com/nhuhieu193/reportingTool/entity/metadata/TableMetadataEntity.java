package com.nhuhieu193.reportingTool.entity.metadata;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "table_metadata")
@Data
public class TableMetadataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tableName;
    private String schemaName;
    private String description;

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColumnMetadataEntity> columns;
}
