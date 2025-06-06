package com.nhuhieu193.reportingTool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableMetadata {
    private String tableName;
    private List<ColumnMetadata> columns;
}
