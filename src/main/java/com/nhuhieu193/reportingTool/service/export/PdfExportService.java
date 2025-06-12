package com.nhuhieu193.reportingTool.service.export;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.nhuhieu193.reportingTool.model.TableMetadata;
import com.nhuhieu193.reportingTool.model.ColumnMetadata;
import com.nhuhieu193.reportingTool.service.metadata.TableMetadataService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfExportService {

    private final TableMetadataService tableMetadataService;

    // Định nghĩa màu sắc
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb ACCENT_COLOR = new DeviceRgb(52, 152, 219);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(248, 249, 250);
    private static final DeviceRgb DARK_GRAY = new DeviceRgb(52, 58, 64);

    public PdfExportService(TableMetadataService tableMetadataService) {
        this.tableMetadataService = tableMetadataService;
    }

    public byte[] exportReportPdf() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Thiết lập margin
            document.setMargins(50, 50, 50, 50);

            // Fonts
            PdfFont titleFont = PdfFontFactory.createFont();
            PdfFont headerFont = PdfFontFactory.createFont();
            PdfFont normalFont = PdfFontFactory.createFont();

            // Tạo trang bìa
            createCoverPage(document, titleFont, headerFont, normalFont);

            // Trang mới cho nội dung
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

            // Lấy dữ liệu tables
            List<TableMetadata> tables = tableMetadataService.getAllTablesWithColumns();

            // Kiểm tra nếu không có data
            if (tables == null || tables.isEmpty()) {
                document.add(new Paragraph("Không có dữ liệu bảng nào trong hệ thống.")
                        .setFont(normalFont)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(12)
                        .setMarginTop(100));
                document.close();
                return baos.toByteArray();
            }

            // Tạo mục lục
            createTableOfContents(document, tables, headerFont, normalFont);

            // Trang mới cho chi tiết
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

            // Chi tiết từng bảng
            createTableDetails(document, tables, headerFont, normalFont);

            // Tạo phụ lục
            createAppendix(document, headerFont, normalFont);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Export PDF failed: " + e.getMessage(), e);
        }
    }

    private void createCoverPage(Document document, PdfFont titleFont, PdfFont headerFont, PdfFont normalFont) {
        // Header với background color
        Div headerDiv = new Div()
                .setBackgroundColor(HEADER_COLOR)
                .setPadding(30)
                .setMarginBottom(40);

        headerDiv.add(new Paragraph("TẬP ĐOÀN CÔNG NGHIỆP - VIỄN THÔNG QUÂN ĐỘI")
                .setFont(headerFont)
                .setFontSize(16)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setMarginBottom(10));

        document.add(headerDiv);

        // Tiêu đề chính
        document.add(new Paragraph("BÁO CÁO THIẾT KẾ CHI TIẾT DỮ LIỆU")
                .setFont(titleFont)
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(DARK_GRAY)
                .setMarginBottom(50));

        // Thông tin báo cáo
        Div infoDiv = new Div()
                .setBorder(new SolidBorder(ACCENT_COLOR, 2))
                .setPadding(20)
                .setMarginTop(50)
                .setMarginBottom(30);

        infoDiv.add(new Paragraph("THÔNG TIN BÁO CÁO")
                .setFont(headerFont)
                .setFontSize(14)
                .setBold()
                .setFontColor(ACCENT_COLOR)
                .setMarginBottom(15));

        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        infoDiv.add(new Paragraph("Ngày tạo: " + currentDateTime)
                .setFont(normalFont)
                .setFontSize(12)
                .setMarginBottom(5));

        infoDiv.add(new Paragraph("Phiên bản: 1.0")
                .setFont(normalFont)
                .setFontSize(12)
                .setMarginBottom(5));

        document.add(infoDiv);

        // Lời mở đầu
        Div introDiv = new Div()
                .setBackgroundColor(LIGHT_GRAY)
                .setPadding(20)
                .setMarginTop(30);

        introDiv.add(new Paragraph("LỜI MỞ ĐẦU")
                .setFont(headerFont)
                .setFontSize(16)
                .setBold()
                .setFontColor(DARK_GRAY)
                .setMarginBottom(15));

        introDiv.add(new Paragraph("Tài liệu này mô tả chi tiết cấu trúc cơ sở dữ liệu của hệ thống, " +
                "bao gồm thông tin về các bảng, cột, kiểu dữ liệu và các ràng buộc. " +
                "Báo cáo được tạo tự động nhằm hỗ trợ việc phát triển và bảo trì hệ thống.")
                .setFont(normalFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.JUSTIFIED));

        document.add(introDiv);
    }

    private void createTableOfContents(Document document, List<TableMetadata> tables, PdfFont headerFont, PdfFont normalFont) {
        // Tiêu đề mục lục
        Div tocHeader = new Div()
                .setBackgroundColor(HEADER_COLOR)
                .setPadding(15)
                .setMarginBottom(20);

        tocHeader.add(new Paragraph("MỤC LỤC")
                .setFont(headerFont)
                .setFontSize(18)
                .setBold()
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(tocHeader);

        // Danh sách bảng
        int pageNumber = 3; // Bắt đầu từ trang 3
        for (TableMetadata table : tables) {
            if (table != null && table.getTableName() != null) {
                Div tableEntry = new Div()
                        .setBorderBottom(new SolidBorder(LIGHT_GRAY, 1))
                        .setPaddingBottom(8)
                        .setPaddingTop(8);

                Paragraph entry = new Paragraph()
                        .add(new Text(table.getTableName())
                                .setFont(normalFont)
                                .setFontSize(12)
                                .setBold())
                        .add(new Tab())
                        .add(new Text(String.valueOf(pageNumber))
                                .setFont(normalFont)
                                .setFontSize(12));

                entry.addTabStops(new TabStop(400, TabAlignment.RIGHT, new DottedLine()));
                tableEntry.add(entry);
                document.add(tableEntry);
                pageNumber++;
            }
        }
    }

    private void createTableDetails(Document document, List<TableMetadata> tables, PdfFont headerFont, PdfFont normalFont) {
        for (TableMetadata table : tables) {
            if (table == null || table.getTableName() == null) {
                continue;
            }

            // Header của bảng
            Div tableHeader = new Div()
                    .setBackgroundColor(ACCENT_COLOR)
                    .setPadding(15)
                    .setMarginBottom(10)
                    .setMarginTop(20);

            tableHeader.add(new Paragraph("Bảng: " + table.getTableName())
                    .setFont(headerFont)
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(ColorConstants.WHITE));

            document.add(tableHeader);

            // Thông tin tổng quan
            if (table.getColumns() != null) {
                document.add(new Paragraph("Số lượng cột: " + table.getColumns().size())
                        .setFont(normalFont)
                        .setFontSize(10)
                        .setFontColor(DARK_GRAY)
                        .setMarginBottom(10));
            }

            // Tạo bảng chi tiết
            Table tbl = new Table(UnitValue.createPercentArray(new float[]{25, 20, 15, 15, 25}))
                    .useAllAvailableWidth()
                    .setBorder(new SolidBorder(DARK_GRAY, 1));

            // Header của bảng dữ liệu
            String[] headers = {"Tên trường", "Kiểu dữ liệu", "Kích thước", "Nullable", "Mô tả"};
            for (String header : headers) {
                Cell headerCell = new Cell()
                        .add(new Paragraph(header)
                                .setFont(headerFont)
                                .setFontSize(11)
                                .setBold()
                                .setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(HEADER_COLOR)
                        .setPadding(8)
                        .setTextAlignment(TextAlignment.CENTER);
                tbl.addHeaderCell(headerCell);
            }

            // Dữ liệu bảng
            if (table.getColumns() != null && !table.getColumns().isEmpty()) {
                boolean isEvenRow = false;
                for (ColumnMetadata col : table.getColumns()) {
                    if (col != null) {
                        DeviceRgb rowColor = isEvenRow ? LIGHT_GRAY : new DeviceRgb(255, 255, 255);

                        // Tên cột
                        Cell nameCell = new Cell()
                                .add(new Paragraph(col.getColumnName() != null ? col.getColumnName() : "")
                                        .setFont(normalFont)
                                        .setFontSize(10)
                                        .setBold())
                                .setBackgroundColor(rowColor)
                                .setPadding(6);
                        tbl.addCell(nameCell);

                        // Kiểu dữ liệu
                        Cell typeCell = new Cell()
                                .add(new Paragraph(col.getDataType() != null ? col.getDataType() : "")
                                        .setFont(normalFont)
                                        .setFontSize(10))
                                .setBackgroundColor(rowColor)
                                .setPadding(6)
                                .setTextAlignment(TextAlignment.CENTER);
                        tbl.addCell(typeCell);

                        // Kích thước
                        Cell sizeCell = new Cell()
                                .add(new Paragraph(col.getColumnSize() > 0 ? String.valueOf(col.getColumnSize()) : "-")
                                        .setFont(normalFont)
                                        .setFontSize(10))
                                .setBackgroundColor(rowColor)
                                .setPadding(6)
                                .setTextAlignment(TextAlignment.CENTER);
                        tbl.addCell(sizeCell);

                        // Nullable
                        String nullableText = Boolean.TRUE.equals(col.getNullable()) ? "YES" : "NO";
                        DeviceRgb nullableColor = Boolean.TRUE.equals(col.getNullable()) ?
                                new DeviceRgb(220, 53, 69) : new DeviceRgb(40, 167, 69);

                        Cell nullableCell = new Cell()
                                .add(new Paragraph(nullableText)
                                        .setFont(normalFont)
                                        .setFontSize(10)
                                        .setBold()
                                        .setFontColor(nullableColor))
                                .setBackgroundColor(rowColor)
                                .setPadding(6)
                                .setTextAlignment(TextAlignment.CENTER);
                        tbl.addCell(nullableCell);

                        // Mô tả (placeholder)
                        Cell descCell = new Cell()
                                .add(new Paragraph("-")
                                        .setFont(normalFont)
                                        .setFontSize(10))
                                .setBackgroundColor(rowColor)
                                .setPadding(6);
                        tbl.addCell(descCell);

                        isEvenRow = !isEvenRow;
                    }
                }
            } else {
                // Nếu không có cột nào
                Cell noDataCell = new Cell(1, 5)
                        .add(new Paragraph("Không có thông tin cột")
                                .setFont(normalFont)
                                .setFontSize(12)
                                .setTextAlignment(TextAlignment.CENTER))
                        .setPadding(20)
                        .setBackgroundColor(LIGHT_GRAY);
                tbl.addCell(noDataCell);
            }

            document.add(tbl);
            document.add(new Paragraph(" ").setMarginBottom(20));
        }
    }

    private void createAppendix(Document document, PdfFont headerFont, PdfFont normalFont) {
        // Trang mới cho phụ lục
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        // Header phụ lục
        Div appendixHeader = new Div()
                .setBackgroundColor(HEADER_COLOR)
                .setPadding(15)
                .setMarginBottom(20);

        appendixHeader.add(new Paragraph("PHỤ LỤC")
                .setFont(headerFont)
                .setFontSize(18)
                .setBold()
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(appendixHeader);

        // Bảng giải thích
        Div explanationDiv = new Div()
                .setBorder(new SolidBorder(ACCENT_COLOR, 1))
                .setPadding(20)
                .setMarginBottom(20);

        explanationDiv.add(new Paragraph("Giải thích ký hiệu:")
                .setFont(headerFont)
                .setFontSize(14)
                .setBold()
                .setFontColor(ACCENT_COLOR)
                .setMarginBottom(15));

        explanationDiv.add(new Paragraph("• YES: Cho phép giá trị NULL (có thể để trống)")
                .setFont(normalFont)
                .setFontSize(12)
                .setMarginLeft(15)
                .setMarginBottom(5));

        explanationDiv.add(new Paragraph("• NO: Không cho phép giá trị NULL (bắt buộc có giá trị)")
                .setFont(normalFont)
                .setFontSize(12)
                .setMarginLeft(15));

        document.add(explanationDiv);

        // Thông tin liên hệ
        Div contactDiv = new Div()
                .setBackgroundColor(LIGHT_GRAY)
                .setPadding(20);

        contactDiv.add(new Paragraph("Thông tin liên hệ:")
                .setFont(headerFont)
                .setFontSize(14)
                .setBold()
                .setFontColor(DARK_GRAY)
                .setMarginBottom(10));

        contactDiv.add(new Paragraph("Đội ngũ phát triển hệ thống")
                .setFont(normalFont)
                .setFontSize(12)
                .setMarginBottom(5));

        contactDiv.add(new Paragraph("Email: support@viettel.com.vn")
                .setFont(normalFont)
                .setFontSize(12));

        document.add(contactDiv);
    }
}