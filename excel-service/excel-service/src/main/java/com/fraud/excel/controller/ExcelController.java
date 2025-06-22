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
import java.util.*;

@RestController
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

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Map<String, Object> data = new HashMap<>();
                data.put("name", row.getCell(0).getStringCellValue());
                data.put("account", row.getCell(1).getStringCellValue());
                data.put("amount", row.getCell(2).getNumericCellValue());
                data.put("mode", row.getCell(3).getStringCellValue());
                data.put("cardUsage", row.getCell(4).getNumericCellValue());
                data.put("age", row.getCell(5).getNumericCellValue());

                String json = mapper.writeValueAsString(data);
                kafkaTemplate.send(topic, json);
            }

            return ResponseEntity.ok("Excel uploaded and sent to Kafka");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }
}
