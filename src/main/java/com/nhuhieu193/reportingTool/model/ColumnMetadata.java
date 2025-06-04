package com.nhuhieu193.reportingTool.model;

import lombok.Data;

@Data
public class ColumnMetadata {
    private String name;
    private String type;
    private int size;
    private boolean nullable;
    private boolean autoIncrement;
    private String defaultValue;
}
