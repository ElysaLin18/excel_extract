package com.example.demo.controllers;


import com.example.demo.models.document;
import com.example.demo.models.document2;
import com.example.demo.models.document3;
import com.example.demo.models.DocumentRepository;
import com.example.demo.models.Document2Repository;
import com.example.demo.models.Document3Repository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.io.*;
import java.util.*;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final Document2Repository document2Repository;
    private final Document3Repository document3Repository;
    @Autowired
    public DocumentService(DocumentRepository documentRepository, Document2Repository document2Repository, Document3Repository document3Repository) {
        this.documentRepository = documentRepository;
        this.document2Repository = document2Repository;
        this.document3Repository = document3Repository;
    }

    public List<document> findAllGeneratedFiles() {
        return documentRepository.findAll();
    }
    

    private File createFilteredExcelFile(MultipartFile file) throws IOException {
        try (
            Workbook originalWorkbook = new XSSFWorkbook(file.getInputStream());
            Workbook newWorkbook = new XSSFWorkbook();
        ) {
            Sheet originalSheet1 = originalWorkbook.getSheetAt(0);
            //test-----------------------------------------------------
            Sheet originalSheet2 = originalWorkbook.getSheetAt(1);
            Sheet originalSheet3 = originalWorkbook.getSheetAt(2);
            //test-----------------------------------------------------
            Sheet newSheet1 = newWorkbook.createSheet("Total");
            Sheet newSheet2 = newWorkbook.createSheet("-1-  Sunday Giving- For New Wes");
            Sheet newSheet3 = newWorkbook.createSheet("-2nd cause - ALL");
            
            //test-----------------------------------------------------
            copySheet(originalSheet1, newSheet1);
            copySheet(originalSheet2, newSheet2);
            copySheet(originalSheet3, newSheet3);
            //test-----------------------------------------------------
            String newFileName = "extract_" + file.getOriginalFilename();
            File newExcelFile = new File(System.getProperty("user.dir") + File.separator + newFileName);
            try (FileOutputStream fileOut = new FileOutputStream(newExcelFile)) {
                newWorkbook.write(fileOut);
            }
    
            return newExcelFile;
        }
    }
    
    private void copySheet(Sheet sourceSheet, Sheet targetSheet) {
        for (int i = 0; i <= sourceSheet.getLastRowNum(); i++) {
            Row sourceRow = sourceSheet.getRow(i);
            Row targetRow = targetSheet.createRow(i);
    
            if (sourceRow != null) {
                for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
                    Cell sourceCell = sourceRow.getCell(j);
                    Cell targetCell = targetRow.createCell(j);
    
                    if (sourceCell != null) {
                        copyCell(sourceCell, targetCell);
                    }
                }
            }
        }
    }

    public String excelToHtmlTable(DocumentRepository docRepo, Long documentId) throws IOException {
        Optional<document> documentOptional = docRepo.findById(documentId);
        if (!documentOptional.isPresent()) {
            return "File not found";
        }

        document doc = documentOptional.get();
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(doc.getContent()))) {
            StringBuilder html = new StringBuilder("<html><body>");
            DataFormatter formatter = new DataFormatter();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                html.append("<h2>").append(sheet.getSheetName()).append("</h2><table border='1'>");

                for (Row row : sheet) {
                    html.append("<tr>");
                    for (Cell cell : row) {
                        html.append("<td>");
                        html.append(formatter.formatCellValue(cell));
                        html.append("</td>");
                    }
                    html.append("</tr>");
                }
                html.append("</table><br>");
            }

            html.append("</body></html>");
            return html.toString();
        }
    }
    public String excelToHtmlTable2(Document2Repository docRepo, Long documentId) throws IOException {
        Optional<document2> documentOptional = docRepo.findById(documentId);
        if (!documentOptional.isPresent()) {
            return "File not found";
        }

        document2 doc = documentOptional.get();
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(doc.getContent()))) {
            StringBuilder html = new StringBuilder("<html><body>");
            DataFormatter formatter = new DataFormatter();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                html.append("<h2>").append(sheet.getSheetName()).append("</h2><table border='1'>");

                for (Row row : sheet) {
                    html.append("<tr>");
                    for (Cell cell : row) {
                        html.append("<td>");
                        html.append(formatter.formatCellValue(cell));
                        html.append("</td>");
                    }
                    html.append("</tr>");
                }
                html.append("</table><br>");
            }

            html.append("</body></html>");
            return html.toString();
        }
    }
    public String excelToHtmlTable3(Document3Repository docRepo, Long documentId) throws IOException {
        Optional<document3> documentOptional = docRepo.findById(documentId);
        if (!documentOptional.isPresent()) {
            return "File not found";
        }

        document3 doc = documentOptional.get();
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(doc.getContent()))) {
            StringBuilder html = new StringBuilder("<html><body>");
            DataFormatter formatter = new DataFormatter();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                html.append("<h2>").append(sheet.getSheetName()).append("</h2><table border='1'>");

                for (Row row : sheet) {
                    html.append("<tr>");
                    for (Cell cell : row) {
                        html.append("<td>");
                        html.append(formatter.formatCellValue(cell));
                        html.append("</td>");
                    }
                    html.append("</tr>");
                }
                html.append("</table><br>");
            }

            html.append("</body></html>");
            return html.toString();
        }
    }

    
    private void copyCell(Cell originalCell, Cell newCell) {
        if (originalCell == null) {
            newCell.setCellValue("");
            return;
        }
    
        switch (originalCell.getCellType()) {
            case STRING:
                newCell.setCellValue(originalCell.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(originalCell)) {
                    newCell.setCellValue(originalCell.getDateCellValue());
                } else {
                    newCell.setCellValue(originalCell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                newCell.setCellValue(originalCell.getBooleanCellValue());
                break;
            case FORMULA:
                newCell.setCellFormula(originalCell.getCellFormula());
                break;
            case BLANK:
                newCell.setBlank();
                break;
            default:
                newCell.setCellValue("Undefined Cell Type");
                break;
        }
    }
    
}
