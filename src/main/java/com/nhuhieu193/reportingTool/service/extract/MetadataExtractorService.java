package com.nhuhieu193.reportingTool.service.extract;

import com.nhuhieu193.reportingTool.model.ColumnMetadata;
import com.nhuhieu193.reportingTool.model.TableMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetadataExtractorService {

    @Autowired
    private DataSource dataSource;

    public List<TableMetadata> extract() throws SQLException {
        List<TableMetadata> tables = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String currentDb = conn.getCatalog();
            ResultSet rsTables = metaData.getTables(currentDb, null, "%", new String[]{"TABLE"});

            while (rsTables.next()) {
                String tableName = rsTables.getString("TABLE_NAME");
                TableMetadata table = new TableMetadata();
                table.setTableName(tableName);

                List<ColumnMetadata> columns = new ArrayList<>();
                ResultSet rsColumns = metaData.getColumns(null, null, tableName, "%");
                while (rsColumns.next()) {
                    ColumnMetadata col = new ColumnMetadata();
                    col.setName(rsColumns.getString("COLUMN_NAME"));
                    col.setType(rsColumns.getString("TYPE_NAME"));
                    col.setSize(rsColumns.getInt("COLUMN_SIZE"));
                    col.setNullable("YES".equals(rsColumns.getString("IS_NULLABLE")));
                    col.setAutoIncrement("YES".equals(rsColumns.getString("IS_AUTOINCREMENT")));
                    col.setDefaultValue(rsColumns.getString("COLUMN_DEF"));
                    columns.add(col);
                }

                table.setColumns(columns);
                tables.add(table);
            }
        }

        return tables;
    }
}
