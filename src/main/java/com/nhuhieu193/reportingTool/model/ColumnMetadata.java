package com.nhuhieu193.reportingTool.model;

public class ColumnMetadata {
    private String columnName;
    private String dataType;
    private Integer columnSize;
    private Boolean nullable;

    public ColumnMetadata() {}

    public ColumnMetadata(String columnName, String dataType, Integer columnSize, Boolean nullable) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.columnSize = columnSize;
        this.nullable = nullable;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(Integer columnSize) {
        this.columnSize = columnSize;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }
}