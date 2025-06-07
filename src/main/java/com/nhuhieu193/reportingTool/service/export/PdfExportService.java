package com.nhuhieu193.reportingTool.service.export;

import com.nhuhieu193.reportingTool.entity.metadata.ColumnMetadataEntity;
import com.nhuhieu193.reportingTool.entity.metadata.TableMetadataEntity;
import com.nhuhieu193.reportingTool.repository.metadata.ColumnMetadataRepository;
import com.nhuhieu193.reportingTool.repository.metadata.TableMetadataRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class PdfExportService {

    private final TableMetadataRepository tableRepo;
    private final ColumnMetadataRepository columnRepo;

    public PdfExportService(TableMetadataRepository tableRepo, ColumnMetadataRepository columnRepo) {
        this.tableRepo = tableRepo;
        this.columnRepo = columnRepo;
    }

    public void exportToPdf(OutputStream out) throws IOException {
        List<TableMetadataEntity> tables = tableRepo.findAll();

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.setFont(PDType1Font.HELVETICA, 12);

            float y = 750;

            for (TableMetadataEntity table : tables) {
                if (y < 100) {
                    content.close();

                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);

                    content = new PDPageContentStream(doc, page);
                    content.setFont(PDType1Font.HELVETICA, 12);
                    y = 750;
                }

                content.beginText();
                content.newLineAtOffset(50, y);
                content.showText("Table: " + table.getTableName());
                content.endText();
                y -= 20;

                List<ColumnMetadataEntity> columns = columnRepo.findByTableName(table.getTableName());
                for (ColumnMetadataEntity col : columns) {
                    if (y < 100) {
                        content.close();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        content = new PDPageContentStream(doc, page);
                        content.setFont(PDType1Font.HELVETICA, 12);
                        y = 750;
                    }

                    content.beginText();
                    content.newLineAtOffset(70, y);
                    content.showText("- " + col.getColumnName() + " : " + col.getDataType());
                    content.endText();
                    y -= 15;
                }

                y -= 10;
            }

            content.close();
            doc.save(out);
        }
    }
}
