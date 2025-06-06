package com.nhuhieu193.reportingTool.entity.metadata;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "column_metadata")
@Data
public class ColumnMetadataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String columnName;
    private String dataType;
    private Integer columnSize;
    private Boolean nullable;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableMetadataEntity table;
}
