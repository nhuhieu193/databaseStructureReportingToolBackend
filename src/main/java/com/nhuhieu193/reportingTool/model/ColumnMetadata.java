package com.nhuhieu193.reportingTool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnMetadata {
    private String columnName;
    private String dataType;
    private int columnSize;
    private boolean nullable;
}
