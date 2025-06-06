package com.nhuhieu193.reportingTool.service.extract;

import com.nhuhieu193.reportingTool.model.ColumnMetadata;
import com.nhuhieu193.reportingTool.model.TableMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MetadataExtractorServiceTest {

    @Mock
    private DatabaseMetaData mockMetaData;

    @Mock
    private ResultSet mockTableResultSet;

    @Mock
    private ResultSet mockColumnResultSet;

    @InjectMocks
    private MetadataExtractorService metadataExtractorService;

    @BeforeEach
    void setup() throws Exception {
        when(mockMetaData.getTables(null, null, "%", new String[]{"TABLE"}))
                .thenReturn(mockTableResultSet);
        when(mockTableResultSet.next()).thenReturn(true, false);
        when(mockTableResultSet.getString("TABLE_NAME")).thenReturn("users");

        when(mockMetaData.getColumns(null, null, "users", "%"))
                .thenReturn(mockColumnResultSet);
        when(mockColumnResultSet.next()).thenReturn(true, false);
        when(mockColumnResultSet.getString("COLUMN_NAME")).thenReturn("id");
        when(mockColumnResultSet.getString("TYPE_NAME")).thenReturn("INTEGER");
        when(mockColumnResultSet.getInt("COLUMN_SIZE")).thenReturn(11);
        when(mockColumnResultSet.getString("IS_NULLABLE")).thenReturn("NO");
    }

    @Test
    void testExtractFromMetaData_singleTableSingleColumn() throws Exception {
        List<TableMetadata> tables = metadataExtractorService.extractFromMetaData(mockMetaData);

        assertEquals(1, tables.size());
        TableMetadata table = tables.get(0);
        assertEquals("users", table.getTableName());

        List<ColumnMetadata> columns = table.getColumns();
        assertEquals(1, columns.size());
        ColumnMetadata column = columns.get(0);
        assertEquals("id", column.getColumnName());
        assertEquals("INTEGER", column.getDataType());
        assertEquals(11, column.getColumnSize());
        assertFalse(column.isNullable());
    }
}
