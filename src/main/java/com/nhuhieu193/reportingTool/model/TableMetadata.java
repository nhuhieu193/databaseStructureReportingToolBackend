package com.nhuhieu193.reportingTool.model;

import lombok.Data;
import java.util.List;

@Data
public class TableMetadata {
    private String tableName;
    private List<ColumnMetadata> columns;
}
