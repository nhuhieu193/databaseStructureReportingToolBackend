package com.nhuhieu193.reportingTool.service.extract;

import com.nhuhieu193.reportingTool.model.ColumnMetadata;
import com.nhuhieu193.reportingTool.model.TableMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetadataExtractorService {

    private final DataSource dataSource;

    public List<TableMetadata> extractDatabaseStructure() {
        List<TableMetadata> tableList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            return extractFromMetaData(metaData);
        } catch (SQLException e) {
            e.printStackTrace(); // bạn có thể log rõ hơn
        }
        return tableList;
    }

    // 👉 Method này dùng trong unit test
    public List<TableMetadata> extractFromMetaData(DatabaseMetaData metaData) throws SQLException {
        List<TableMetadata> tableList = new ArrayList<>();

        ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            List<ColumnMetadata> columnList = new ArrayList<>();

            ResultSet columns = metaData.getColumns(null, null, tableName, "%");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                int columnSize = columns.getInt("COLUMN_SIZE");
                boolean nullable = "YES".equalsIgnoreCase(columns.getString("IS_NULLABLE"));

                ColumnMetadata column = new ColumnMetadata(columnName, dataType, columnSize, nullable);
                columnList.add(column);
            }

            TableMetadata table = new TableMetadata(tableName, columnList);
            tableList.add(table);
        }

        return tableList;
    }
}
