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
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.properties.*;
import com.nhuhieu193.reportingTool.model.TableMetadata;
import com.nhuhieu193.reportingTool.model.ColumnMetadata;
import com.nhuhieu193.reportingTool.service.metadata.TableMetadataService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class PdfExportService {

    private final TableMetadataService tableMetadataService;

    // Định nghĩa màu sắc theo mẫu Viettel
    private static final DeviceRgb VIETTEL_RED = new DeviceRgb(227, 24, 55);
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb LIGHT_BLUE = new DeviceRgb(240, 248, 255);
    private static final DeviceRgb DARK_GRAY = new DeviceRgb(33, 37, 41);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(248, 249, 250);
    private static final DeviceRgb TABLE_HEADER = new DeviceRgb(52, 58, 64);

    public PdfExportService(TableMetadataService tableMetadataService) {
        this.tableMetadataService = tableMetadataService;
    }

    public byte[] exportReportPdf() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Thiết lập margin
            document.setMargins(40, 40, 40, 40);

            // QUAN TRỌNG: Sử dụng font hỗ trợ tiếng Việt
            PdfFont titleFont = createVietnameseFont(true);
            PdfFont headerFont = createVietnameseFont(true);
            PdfFont normalFont = createVietnameseFont(false);

            // Tạo trang bìa theo mẫu Viettel
            createViettelCoverPage(document, titleFont, headerFont, normalFont);

            // Trang giới thiệu
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            createIntroductionPage(document, headerFont, normalFont);

            // Lấy dữ liệu tables
            ArrayList<TableMetadata> tables = new ArrayList<>(tableMetadataService.getAllTablesWithColumns());

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
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            createTableOfContents(document, tables, headerFont, normalFont);

            // Chi tiết từng bảng
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            createTableDetails(document, tables, headerFont, normalFont);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Export PDF failed: " + e.getMessage(), e);
        }
    }

    /**
     * Tạo font hỗ trợ tiếng Việt
     */
    private PdfFont createVietnameseFont(boolean isBold) throws IOException {
        try {
            // Cách 1: Sử dụng font có sẵn trong hệ thống (khuyến nghị)
            if (isBold) {
                return PdfFontFactory.createFont("src/main/resources/fonts/DejaVuSans-Bold.ttf", "Identity-H");
            } else {
                return PdfFontFactory.createFont("src/main/resources/fonts/DejaVuSans.ttf", "Identity-H");
            }
        } catch (Exception e) {
            try {
                // Cách 2: Sử dụng font từ classpath
                if (isBold) {
                    return PdfFontFactory.createFont("fonts/DejaVuSans-Bold.ttf", "Identity-H");
                } else {
                    return PdfFontFactory.createFont("fonts/DejaVuSans.ttf", "Identity-H");
                }
            } catch (Exception e2) {
                try {
                    // Cách 3: Sử dụng font hệ thống Windows (nếu có)
                    return PdfFontFactory.createFont("c:/windows/fonts/arial.ttf", "Identity-H");
                } catch (Exception e3) {
                    // Fallback: Sử dụng font built-in với encoding đặc biệt
                    System.out.println("Warning: Vietnamese font not found, using fallback font");
                    return PdfFontFactory.createFont("Helvetica", "Identity-H");
                }
            }
        }
    }

    private void createViettelCoverPage(Document document, PdfFont titleFont, PdfFont headerFont, PdfFont normalFont) {
        try {
            // Header ID và version với spacing tốt hơn
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                    .useAllAvailableWidth();

            headerTable.addCell(new Cell()
                    .add(new Paragraph("id - Thiết kế chi tiết dữ liệu")
                            .setFont(normalFont)
                            .setFontSize(10))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(0));

            headerTable.addCell(new Cell()
                    .add(new Paragraph("v1.3")
                            .setFont(normalFont)
                            .setFontSize(10))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(0)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(headerTable);

            // Spacer
            document.add(new Paragraph(" ").setMarginTop(60));

            // Logo Viettel với spacing tốt hơn
            Paragraph viettelLogo = new Paragraph("viettel")
                    .setFont(titleFont)
                    .setFontSize(42)
                    .setBold()
                    .setFontColor(VIETTEL_RED)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(viettelLogo);

            Paragraph tagline = new Paragraph("your way")
                    .setFont(normalFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(50);
            document.add(tagline);

            // Tên tập đoàn với line height tốt hơn
            Paragraph company1 = new Paragraph("TẬP ĐOÀN CÔNG NGHIỆP - VIỄN THÔNG QUÂN ĐỘI")
                    .setFont(headerFont)
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(8);
            document.add(company1);

            Paragraph company2 = new Paragraph("TỔNG CÔNG TY GIẢI PHÁP VÀ DOANH NGHIỆP")
                    .setFont(headerFont)
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(70);
            document.add(company2);

            // Thông tin sản phẩm
            Paragraph productInfo = new Paragraph("XÂY DỰNG SẢN PHẨM")
                    .setFont(normalFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(25);
            document.add(productInfo);

            Paragraph systemName = new Paragraph("HỆ THỐNG XÂY DỰNG BÁO CÁO ĐỘNG")
                    .setFont(headerFont)
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(70);
            document.add(systemName);

            Paragraph docTitle = new Paragraph("TÀI LIỆU THIẾT KẾ CHI TIẾT DỮ LIỆU")
                    .setFont(titleFont)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(90);
            document.add(docTitle);

            // Thông tin metadata với layout tốt hơn
            Table metaTable = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                    .useAllAvailableWidth()
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setWidth(UnitValue.createPercentValue(50));

            // Mã hiệu sản phẩm
            metaTable.addCell(new Cell()
                    .add(new Paragraph("Mã hiệu sản phẩm:")
                            .setFont(normalFont)
                            .setFontSize(11))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(5));
            metaTable.addCell(new Cell()
                    .add(new Paragraph("________________________")
                            .setFont(normalFont)
                            .setFontSize(11))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(5));

            // Mã hiệu tài liệu
            metaTable.addCell(new Cell()
                    .add(new Paragraph("Mã hiệu tài liệu:")
                            .setFont(normalFont)
                            .setFontSize(11))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(5));
            metaTable.addCell(new Cell()
                    .add(new Paragraph("________________________")
                            .setFont(normalFont)
                            .setFontSize(11))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(5));

            document.add(metaTable);

            // Footer với khoảng cách phù hợp
            document.add(new Paragraph(" ").setMarginTop(80));
            Paragraph footer = new Paragraph("Hà Nội, năm 2024")
                    .setFont(normalFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(footer);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating cover page: " + e.getMessage(), e);
        }
    }

    private void createIntroductionPage(Document document, PdfFont headerFont, PdfFont normalFont) {
        try {
            // Tiêu đề chương
            Paragraph chapterTitle = new Paragraph("1. GIỚI THIỆU")
                    .setFont(headerFont)
                    .setFontSize(16)
                    .setBold()
                    .setMarginBottom(25);
            document.add(chapterTitle);

            // 1.1 Mục tiêu tài liệu
            Paragraph section1_1 = new Paragraph("1.1    Mục tiêu tài liệu")
                    .setFont(headerFont)
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(15);
            document.add(section1_1);

            // Bullet points với định dạng tốt hơn
            List bulletList = new List()
                    .setSymbolIndent(15)
                    .setListSymbol("•")
                    .setMarginLeft(20)
                    .setMarginBottom(20);

            bulletList.add((ListItem) new ListItem("Tài liệu này nhằm mục đích mô tả thiết kế chi tiết dữ liệu trên hệ thống giám sát và xử lý vi phạm trật tự an toàn giao thông.")
                    .setFont(normalFont)
                    .setFontSize(11)
                    .setMarginBottom(8));

            bulletList.add((ListItem) new ListItem("Tài liệu này mô tả thiết kế cơ sở dữ liệu logic và vật lý, thiết kế ràng buộc dữ liệu giữa các bảng trong cơ sở dữ liệu.")
                    .setFont(normalFont)
                    .setFontSize(11)
                    .setMarginBottom(8));

            bulletList.add((ListItem) new ListItem("Tài liệu này được sử dụng để:")
                    .setFont(normalFont)
                    .setFontSize(11)
                    .setMarginBottom(5));

            document.add(bulletList);

            // Sub-bullets với indent tốt hơn
            List subBulletList = new List()
                    .setSymbolIndent(15)
                    .setListSymbol("○")
                    .setMarginLeft(40)
                    .setMarginBottom(25);

            subBulletList.add((ListItem) new ListItem("Căn cứ kiểm thử: lập kịch bản kiểm thử và số liệu kiểm thử.")
                    .setFont(normalFont)
                    .setFontSize(11)
                    .setMarginBottom(5));

            subBulletList.add((ListItem) new ListItem("Căn cứ lập trình: xây dựng hệ thống thông tin phục vụ họp và xử lý công việc của chính phủ.")
                    .setFont(normalFont)
                    .setFontSize(11)
                    .setMarginBottom(5));

            document.add(subBulletList);

            // 1.2 Định nghĩa thuật ngữ
            Paragraph section1_2 = new Paragraph("1.2    Định nghĩa thuật ngữ và các từ viết tắt")
                    .setFont(headerFont)
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(15)
                    .setMarginTop(20);
            document.add(section1_2);

            // Bảng định nghĩa thuật ngữ với style đẹp hơn
            Table termTable = new Table(UnitValue.createPercentArray(new float[]{25, 35, 40}))
                    .useAllAvailableWidth()
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                    .setMarginBottom(25);

            // Header
            termTable.addHeaderCell(createTableHeaderCell("Thuật ngữ", headerFont));
            termTable.addHeaderCell(createTableHeaderCell("Định nghĩa", headerFont));
            termTable.addHeaderCell(createTableHeaderCell("Ghi chú", headerFont));

            // Thêm các dòng trống
            for (int i = 0; i < 5; i++) {
                termTable.addCell(createTableDataCell(" ", normalFont, 12));
                termTable.addCell(createTableDataCell(" ", normalFont, 12));
                termTable.addCell(createTableDataCell(" ", normalFont, 12));
            }

            document.add(termTable);

            // 1.3 Tài liệu tham khảo
            Paragraph section1_3 = new Paragraph("1.3    Tài liệu tham khảo")
                    .setFont(headerFont)
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(15)
                    .setMarginTop(20);
            document.add(section1_3);

            // Bảng tài liệu tham khảo
            Table refTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}))
                    .useAllAvailableWidth()
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 1));

            refTable.addHeaderCell(createTableHeaderCell("Tên tài liệu", headerFont));
            refTable.addHeaderCell(createTableHeaderCell("Ngày phát hành", headerFont));
            refTable.addHeaderCell(createTableHeaderCell("Nguồn", headerFont));
            refTable.addHeaderCell(createTableHeaderCell("Ghi chú", headerFont));

            String[] refData = {
                    "Tài liệu yêu cầu hệ thống", "15/06/2024", "Viettel Solutions", "",
                    "Tài liệu phân tích thiết kế", "20/06/2024", "Viettel Solutions", "",
                    "Tiêu chuẩn thiết kế CSDL", "01/06/2024", "Bộ TT&TT", ""
            };

            for (int i = 0; i < refData.length; i += 4) {
                refTable.addCell(createTableDataCell(refData[i], normalFont, 10));
                refTable.addCell(createTableDataCell(refData[i + 1], normalFont, 10));
                refTable.addCell(createTableDataCell(refData[i + 2], normalFont, 10));
                refTable.addCell(createTableDataCell(refData[i + 3], normalFont, 10));
            }

            document.add(refTable);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating introduction page: " + e.getMessage(), e);
        }
    }

    // Helper methods để tạo cell đẹp hơn
    private Cell createTableHeaderCell(String content, PdfFont font) {
        return new Cell()
                .add(new Paragraph(content)
                        .setFont(font)
                        .setFontSize(11)
                        .setBold()
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(TABLE_HEADER)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
    }

    private Cell createTableDataCell(String content, PdfFont font, int height) {
        return new Cell()
                .add(new Paragraph(content)
                        .setFont(font)
                        .setFontSize(10))
                .setPadding(5)
                .setMinHeight(height);
    }

    private void createTableOfContents(Document document, ArrayList<TableMetadata> tables, PdfFont headerFont, PdfFont normalFont) {
        try {
            // Tiêu đề mục lục
            Paragraph tocTitle = new Paragraph("MỤC LỤC")
                    .setFont(headerFont)
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(40);
            document.add(tocTitle);

            // Tạo table cho mục lục để alignment đẹp hơn
            Table tocTable = new Table(UnitValue.createPercentArray(new float[]{85, 15}))
                    .useAllAvailableWidth();

            // Mục giới thiệu
            tocTable.addCell(new Cell()
                    .add(new Paragraph("1. GIỚI THIỆU")
                            .setFont(normalFont)
                            .setFontSize(12))
                    .setBorder(Border.NO_BORDER)
                    .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(8));
            tocTable.addCell(new Cell()
                    .add(new Paragraph("2")
                            .setFont(normalFont)
                            .setFontSize(12))
                    .setBorder(Border.NO_BORDER)
                    .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.RIGHT));

            // Mục chi tiết bảng
            tocTable.addCell(new Cell()
                    .add(new Paragraph("2. CHI TIẾT CÁC BẢNG DỮ LIỆU")
                            .setFont(normalFont)
                            .setFontSize(12))
                    .setBorder(Border.NO_BORDER)
                    .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(8));
            tocTable.addCell(new Cell()
                    .add(new Paragraph("4")
                            .setFont(normalFont)
                            .setFontSize(12))
                    .setBorder(Border.NO_BORDER)
                    .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.RIGHT));

            int pageNumber = 5;
            for (int i = 0; i < tables.size(); i++) {
                TableMetadata table = tables.get(i);
                if (table != null && table.getTableName() != null) {
                    tocTable.addCell(new Cell()
                            .add(new Paragraph("    2." + (i + 1) + " " + table.getTableName())
                                    .setFont(normalFont)
                                    .setFontSize(11))
                            .setBorder(Border.NO_BORDER)
                            .setPadding(5));
                    tocTable.addCell(new Cell()
                            .add(new Paragraph(String.valueOf(pageNumber))
                                    .setFont(normalFont)
                                    .setFontSize(11))
                            .setBorder(Border.NO_BORDER)
                            .setPadding(5)
                            .setTextAlignment(TextAlignment.RIGHT));
                    pageNumber++;
                }
            }

            document.add(tocTable);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating table of contents: " + e.getMessage(), e);
        }
    }

    private void createTableDetails(Document document, ArrayList<TableMetadata> tables, PdfFont headerFont, PdfFont normalFont) {
        try {
            // Tiêu đề chương
            Paragraph chapterTitle = new Paragraph("2. CHI TIẾT CÁC BẢNG DỮ LIỆU")
                    .setFont(headerFont)
                    .setFontSize(16)
                    .setBold()
                    .setMarginBottom(30);
            document.add(chapterTitle);

            int tableIndex = 1;
            for (TableMetadata table : tables) {
                if (table == null || table.getTableName() == null) {
                    continue;
                }

                // Tiêu đề bảng
                Paragraph tableTitle = new Paragraph("2." + tableIndex + " " + table.getTableName())
                        .setFont(headerFont)
                        .setFontSize(14)
                        .setBold()
                        .setMarginTop(25)
                        .setMarginBottom(15);
                document.add(tableTitle);

                // Thông tin tổng quan
                if (table.getColumns() != null) {
                    document.add(new Paragraph("Số lượng cột: " + table.getColumns().size())
                            .setFont(normalFont)
                            .setFontSize(11)
                            .setMarginBottom(15));
                }

                // Tạo bảng chi tiết với style đẹp hơn
                Table tbl = new Table(UnitValue.createPercentArray(new float[]{25, 20, 15, 15, 25}))
                        .useAllAvailableWidth()
                        .setBorder(new SolidBorder(TABLE_HEADER, 1));

                // Header của bảng
                String[] headers = {"Tên trường", "Kiểu dữ liệu", "Kích thước", "Nullable", "Mô tả"};
                for (String header : headers) {
                    tbl.addHeaderCell(createTableHeaderCell(header, headerFont));
                }

                // Dữ liệu bảng
                if (table.getColumns() != null && !table.getColumns().isEmpty()) {
                    boolean isEvenRow = false;
                    for (ColumnMetadata col : table.getColumns()) {
                        if (col != null) {
                            DeviceRgb rowColor = isEvenRow ? LIGHT_BLUE : (DeviceRgb) ColorConstants.WHITE;

                            // Tên cột
                            tbl.addCell(new Cell()
                                    .add(new Paragraph(col.getColumnName() != null ? col.getColumnName() : "")
                                            .setFont(normalFont)
                                            .setFontSize(10)
                                            .setBold())
                                    .setBackgroundColor(rowColor)
                                    .setPadding(8));

                            // Kiểu dữ liệu
                            tbl.addCell(new Cell()
                                    .add(new Paragraph(col.getDataType() != null ? col.getDataType() : "")
                                            .setFont(normalFont)
                                            .setFontSize(10))
                                    .setBackgroundColor(rowColor)
                                    .setPadding(8)
                                    .setTextAlignment(TextAlignment.CENTER));

                            // Kích thước
                            tbl.addCell(new Cell()
                                    .add(new Paragraph(col.getColumnSize() > 0 ? String.valueOf(col.getColumnSize()) : "-")
                                            .setFont(normalFont)
                                            .setFontSize(10))
                                    .setBackgroundColor(rowColor)
                                    .setPadding(8)
                                    .setTextAlignment(TextAlignment.CENTER));

                            // Nullable
                            String nullableText = Boolean.TRUE.equals(col.getNullable()) ? "YES" : "NO";
                            DeviceRgb nullableColor = Boolean.TRUE.equals(col.getNullable()) ?
                                    new DeviceRgb(220, 53, 69) : new DeviceRgb(40, 167, 69);

                            tbl.addCell(new Cell()
                                    .add(new Paragraph(nullableText)
                                            .setFont(normalFont)
                                            .setFontSize(10)
                                            .setBold()
                                            .setFontColor(nullableColor))
                                    .setBackgroundColor(rowColor)
                                    .setPadding(8)
                                    .setTextAlignment(TextAlignment.CENTER));

                            // Mô tả
                            tbl.addCell(new Cell()
                                    .add(new Paragraph("-")
                                            .setFont(normalFont)
                                            .setFontSize(10))
                                    .setBackgroundColor(rowColor)
                                    .setPadding(8));

                            isEvenRow = !isEvenRow;
                        }
                    }
                } else {
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
                document.add(new Paragraph(" ").setMarginBottom(25));

                // Thêm page break sau mỗi 2 bảng
                if (tableIndex % 2 == 0 && tableIndex < tables.size()) {
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }

                tableIndex++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating table details: " + e.getMessage(), e);
        }
    }
}