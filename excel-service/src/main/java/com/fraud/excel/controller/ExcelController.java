package com.fraud.excel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/excel")
public class ExcelController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${topic.name}")
    private String topic;

    public ExcelController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                return ResponseEntity.badRequest().body("Excel file is empty.");
            }

            // Read the header row to map column names to indexes
            Row headerRow = rowIterator.next();
            Map<String, Integer> headerMap = new HashMap<>();

            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim().toLowerCase();
                headerMap.put(header, cell.getColumnIndex());
            }

            // Required headers
            String[] requiredHeaders = {"transactionid", "name", "accountnumber", "amount", "mode", "cardusage", "age"};
            for (String header : requiredHeaders) {
                if (!headerMap.containsKey(header)) {
                    return ResponseEntity.badRequest()
                        .body("Missing required column: " + header);
                }
            }
            
            long uploadTimestamp = System.currentTimeMillis();	//new

            // Process each row using the mapped headers
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, Object> data = new HashMap<>();

                try {
                    data.put("transactionID", getCellValue(row.getCell(headerMap.get("transactionid"))));
                    data.put("name", getCellValue(row.getCell(headerMap.get("name"))));
                    data.put("accountNumber", getCellValue(row.getCell(headerMap.get("accountnumber"))));
                    data.put("amount", Double.parseDouble(getCellValue(row.getCell(headerMap.get("amount")))));
                    data.put("mode", getCellValue(row.getCell(headerMap.get("mode"))));
                    data.put("cardUsage", Double.parseDouble(getCellValue(row.getCell(headerMap.get("cardusage")))));
                    data.put("age", Double.parseDouble(getCellValue(row.getCell(headerMap.get("age")))));
                    data.put("uploadTimestamp", uploadTimestamp);	//new

                    String json = mapper.writeValueAsString(data);
                    kafkaTemplate.send(topic, json);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Skipping row due to error: " + row.getRowNum());
                }
            }

            return ResponseEntity.ok("Excel uploaded and sent to Kafka");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString(); // FIXED LINE
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }

}
