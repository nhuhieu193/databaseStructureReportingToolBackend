package com.nhuhieu193.reportingTool.service.extract;

import com.nhuhieu193.reportingTool.entity.metadata.ColumnMetadataEntity;
import com.nhuhieu193.reportingTool.entity.metadata.TableMetadataEntity;
import com.nhuhieu193.reportingTool.repository.metadata.TableMetadataRepository;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetadataExtractorService {

    private final JdbcTemplate extractJdbcTemplate;
    private final TableMetadataRepository tableMetadataRepository;

    public MetadataExtractorService(
            JdbcTemplate extractJdbcTemplate,
            TableMetadataRepository tableMetadataRepository) {
        this.extractJdbcTemplate = extractJdbcTemplate;
        this.tableMetadataRepository = tableMetadataRepository;
    }

    @Transactional
    public void extractAndSyncMetadata() throws SQLException {
        // ✅ Dùng try-with-resources để đảm bảo connection được đóng
        try (Connection conn = extractJdbcTemplate.getDataSource().getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            List<TableMetadataEntity> tables = extractFromMetaData(metaData);

            tableMetadataRepository.deleteAll();
            tableMetadataRepository.saveAll(tables);
        }
    }

    public List<TableMetadataEntity> extractFromMetaData(DatabaseMetaData metaData) throws SQLException {
        List<TableMetadataEntity> tableList = new ArrayList<>();

        try (ResultSet tables = metaData.getTables("classicmodels", null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");

                List<ColumnMetadataEntity> columnList = new ArrayList<>();
                try (ResultSet columns = metaData.getColumns(null, null, tableName, "%")) {
                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        String dataType = columns.getString("TYPE_NAME");
                        int columnSize = columns.getInt("COLUMN_SIZE");
                        boolean nullable = "YES".equalsIgnoreCase(columns.getString("IS_NULLABLE"));

                        ColumnMetadataEntity column = new ColumnMetadataEntity();
                        column.setColumnName(columnName);
                        column.setDataType(dataType);
                        column.setColumnSize(columnSize);
                        column.setNullable(nullable);

                        columnList.add(column);
                    }
                }

                TableMetadataEntity table = new TableMetadataEntity();
                table.setTableName(tableName);
                table.setColumns(columnList);
                columnList.forEach(c -> c.setTable(table));

                tableList.add(table);
            }
        }

        return tableList;
    }
}
