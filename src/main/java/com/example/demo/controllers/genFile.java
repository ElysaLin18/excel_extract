package com.example.demo.controllers;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class genFile {
    @Autowired
    public ByteArrayOutputStream generateFile(byte[] rawFileContent, byte[] nameFileContent) throws IOException {
        try (
            ByteArrayInputStream rawBis = new ByteArrayInputStream(rawFileContent);
            Workbook rawWorkbook = new XSSFWorkbook(rawBis);
            Workbook generatedWorkbook = new XSSFWorkbook();
            ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ) {
            Map<String, String> nameIdMap = createNameIdMap(nameFileContent);
    
            Sheet firstSheet = rawWorkbook.getSheetAt(0);
            copySheetToWorkbook(firstSheet, generatedWorkbook, firstSheet.getSheetName());
            processAndCopySheet(rawWorkbook.getSheetAt(1), generatedWorkbook, "-1-  Sunday Giving- For New Wes", nameIdMap);
    
            Sheet combinedSheet = generatedWorkbook.createSheet("-2nd cause - ALL");

            for (int i = 2; i <= 5; i++) {
                Sheet rawSheet = rawWorkbook.getSheetAt(i);
                Row row = rawSheet.getRow(1);
                if (row != null) {
                    Cell cell = row.getCell(8);
                    if (cell != null) {
                        int newRowNum = combinedSheet.getLastRowNum() + 1;
                        Row combinedRow = combinedSheet.createRow(newRowNum);
                        Cell newCell = combinedRow.createCell(0);
                        copyCell(cell, newCell, nameIdMap);
                    }
                }
                processAndCopyColumns(rawSheet, combinedSheet, nameIdMap);
            }
            
    
            generatedWorkbook.write(bos);
            return bos;
        }
    }

    private void processAndCopyColumns(Sheet originalSheet, Sheet combinedSheet, Map<String, String> nameIdMap) {
        int[] columnsToKeepIndexes = {0, 9, 10, 11, 12, 14, 15, 16};
    
        for (Row row : originalSheet) {
            int lastRowNum = combinedSheet.getLastRowNum();
            Row newRow = combinedSheet.createRow(lastRowNum + 1);
            
            for (int i = 0; i < columnsToKeepIndexes.length; i++) {
                int oldCellIndex = columnsToKeepIndexes[i];
                Cell oldCell = row.getCell(oldCellIndex);
                if (oldCell != null) {
                    Cell newCell = newRow.createCell(i);
                    copyCell(oldCell, newCell, nameIdMap);
                }
            }
    
            Cell nameCell = newRow.getCell(0);
            if (nameCell != null && nameCell.getCellType() == CellType.STRING) {
                String originalValue = nameCell.getStringCellValue();
                String envelopeNumber = extractNumericPart(originalValue);
            
                if (!envelopeNumber.isEmpty() && !envelopeNumber.equals(originalValue)) {
                    newRow.createCell(5).setCellValue(envelopeNumber);
                } 
                for (int colIndex = newRow.getLastCellNum(); colIndex < originalSheet.getRow(0).getLastCellNum(); colIndex++) {
                    Cell cell = newRow.createCell(colIndex);
                    cell.setCellValue("");
                }
            }
        }
    }

    private String extractNumericPart(String name) {
        String[] parts = name.split("-");
        if (parts.length > 1) {
            String possibleNumber = parts[parts.length - 1].trim();
            try {
                Integer.parseInt(possibleNumber);
                return possibleNumber;
            } catch (NumberFormatException e) {
            }
        }
        return "";
    }
    
    
    
    private void processAndCopySheet(Sheet originalSheet, Workbook generatedWorkbook, String newSheetName, Map<String, String> nameIdMap) {
        Sheet newSheet = generatedWorkbook.createSheet(newSheetName);
        int[] columnsToKeepIndexes = {0, 9, 10, 11, 12, 14, 15, 16};
    
        for (Row row : originalSheet) {
            Row newRow = newSheet.createRow(row.getRowNum());
            int newCellIndex = 0;
    
            for (int oldCellIndex : columnsToKeepIndexes) {
                Cell oldCell = row.getCell(oldCellIndex);
                if (oldCell != null) {
                    Cell newCell = newRow.createCell(newCellIndex++);
                    copyCell(oldCell, newCell, nameIdMap);
                }
            }
    
            Cell nameCell = newRow.getCell(0);
            if (nameCell != null && nameCell.getCellType() == CellType.STRING) {
                String originalValue = nameCell.getStringCellValue();
                String envelopeNumber = extractNumericPart(originalValue);
            
                if (!envelopeNumber.isEmpty() && !envelopeNumber.equals(originalValue)) {
                    newRow.createCell(5).setCellValue(envelopeNumber);
                } 
                for (int colIndex = newRow.getLastCellNum(); colIndex < originalSheet.getRow(0).getLastCellNum(); colIndex++) {
                    Cell cell = newRow.createCell(colIndex);
                    cell.setCellValue("");
                }
            }
        }
    }
    
    

    private void copyCell(Cell oldCell, Cell newCell, Map<String, String> nameIdMap) {
        CellStyle newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
        newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
        newCell.setCellStyle(newCellStyle);
        
        switch (oldCell.getCellType()) {
            case STRING:
                String cellValue = oldCell.getStringCellValue();
                if (nameIdMap.containsKey(cellValue)) {
                    newCell.setCellValue(nameIdMap.get(cellValue));
                } else {
                    newCell.setCellValue(cellValue);
                }
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(oldCell)) {
                    newCell.setCellValue(oldCell.getDateCellValue());
                } else {
                    newCell.setCellValue(oldCell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                newCell.setCellValue(oldCell.getBooleanCellValue());
                break;
            case FORMULA:
                newCell.setCellFormula(oldCell.getCellFormula());
                break;
            case ERROR:
                newCell.setCellErrorValue(oldCell.getErrorCellValue());
                break;
            case BLANK:
                newCell.setBlank();
                break;
            default:
                throw new IllegalArgumentException("Unsupported cell type: " + oldCell.getCellType());
        }
    }

    private void copySheetToWorkbook(Sheet sheetToCopy, Workbook workbook, String sheetName) {
        Sheet newSheet = workbook.createSheet(sheetName);
        for (int i = 0; i <= sheetToCopy.getLastRowNum(); i++) {
            Row sourceRow = sheetToCopy.getRow(i);
            if (sourceRow != null) {
                Row newRow = newSheet.createRow(i);
                for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
                    Cell sourceCell = sourceRow.getCell(j);
                    if (sourceCell != null) {
                        Cell newCell = newRow.createCell(j);
                        copyCellContent(sourceCell, newCell);
                        copyCellStyle(sourceCell, newCell);
                    }
                }
            }
        }
    }

    private Map<String, String> createNameIdMap(byte[] nameFileContent) {
        Map<String, String> nameIdMap = new HashMap<>();

        try (Workbook namesWorkbook = new XSSFWorkbook(new ByteArrayInputStream(nameFileContent))) {
            Sheet namesSheet = namesWorkbook.getSheetAt(0);
            for (Row row : namesSheet) {
                Cell nameCell = row.getCell(0);
                Cell idCell = row.getCell(1);
                if (nameCell != null && idCell != null) {
                    nameIdMap.put(nameCell.getStringCellValue(), idCell.getStringCellValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nameIdMap;
    }

    private void copyCellContent(Cell sourceCell, Cell newCell) {
        switch (sourceCell.getCellType()) {
            case BLANK:
                newCell.setCellValue("");
                break;
            case BOOLEAN:
                newCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case ERROR:
                newCell.setCellErrorValue(sourceCell.getErrorCellValue());
                break;
            case FORMULA:
                newCell.setCellFormula(sourceCell.getCellFormula());
                break;
            case NUMERIC:
                newCell.setCellValue(sourceCell.getNumericCellValue());
                break;
            case STRING:
                newCell.setCellValue(sourceCell.getStringCellValue());
                break;
            default:
                break;
        }
    }
    
    private void copyCellStyle(Cell sourceCell, Cell newCell) {
        Workbook workbook = newCell.getSheet().getWorkbook();
        CellStyle newCellStyle = workbook.createCellStyle();
        newCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
        newCell.setCellStyle(newCellStyle);
    }
}