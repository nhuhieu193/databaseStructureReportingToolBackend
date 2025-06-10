package com.nhuhieu193.reportingTool.entity.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private TableMetadataEntity table;
}
