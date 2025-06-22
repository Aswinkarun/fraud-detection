package com.fraud.excel.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelService {
    public String processExcel(MultipartFile file) {
        // Dummy return for now
        return "File received successfully and processing started!";
    }
}
