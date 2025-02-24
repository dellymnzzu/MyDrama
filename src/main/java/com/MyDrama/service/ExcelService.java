package com.MyDrama.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    public ByteArrayInputStream generateExcelReport() throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // 스타일 정의
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            // 매출 현황 시트
            Sheet salesSheet = workbook.createSheet("매출 현황");
            createSalesSheet(salesSheet, headerStyle, dataStyle);
            
            // 제품 카테고리 분석 시트
            Sheet categorySheet = workbook.createSheet("제품 카테고리 분석");
            createCategorySheet(categorySheet, headerStyle, dataStyle);
            
            // 인기 제품 시트
            Sheet popularSheet = workbook.createSheet("인기 제품 TOP 10");
            createPopularProductsSheet(popularSheet, headerStyle, dataStyle);
            
            // 열 너비 자동 조정
            for (Sheet sheet : new Sheet[]{salesSheet, categorySheet, popularSheet}) {
                for (int i = 0; i < 10; i++) {
                    sheet.autoSizeColumn(i);
                }
            }
            
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private void createSalesSheet(Sheet sheet, CellStyle headerStyle, CellStyle dataStyle) {
        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        List<String> headers = Arrays.asList("날짜", "총 매출", "판매 수량", "평균 구매가");
        
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        // 더미 데이터 생성
        Object[][] data = {
            {"2024-02-01", 1500000, 45, 33333},
            {"2024-02-02", 2100000, 63, 33333},
            {"2024-02-03", 1800000, 54, 33333},
            {"2024-02-04", 2300000, 69, 33333},
            {"2024-02-05", 1900000, 57, 33333}
        };

        // 데이터 입력
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data[i][j].toString());
                cell.setCellStyle(dataStyle);
            }
        }
    }

    private void createCategorySheet(Sheet sheet, CellStyle headerStyle, CellStyle dataStyle) {
        Row headerRow = sheet.createRow(0);
        List<String> headers = Arrays.asList("카테고리", "판매량", "매출액", "평균 평점");
        
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        // 더미 데이터
        Object[][] data = {
            {"스킨케어", 1200, 24000000, 4.5},
            {"메이크업", 800, 16000000, 4.3},
            {"클렌징", 500, 7500000, 4.7},
            {"선케어", 300, 6000000, 4.6},
            {"마스크팩", 400, 4000000, 4.4}
        };

        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data[i][j].toString());
                cell.setCellStyle(dataStyle);
            }
        }
    }

    private void createPopularProductsSheet(Sheet sheet, CellStyle headerStyle, CellStyle dataStyle) {
        Row headerRow = sheet.createRow(0);
        List<String> headers = Arrays.asList("순위", "제품명", "카테고리", "판매량", "평점");
        
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        // 더미 데이터
        Object[][] data = {
            {1, "수분크림A", "스킨케어", 500, 4.8},
            {2, "립스틱B", "메이크업", 450, 4.7},
            {3, "토너C", "스킨케어", 400, 4.6},
            {4, "선크림D", "선케어", 350, 4.8},
            {5, "클렌징폼E", "클렌징", 300, 4.5}
        };

        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data[i][j].toString());
                cell.setCellStyle(dataStyle);
            }
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
