package com.yeogi.scms.controller;

import com.yeogi.scms.service.DataInitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DataInitializationController {

    private final DataInitializationService dataInitializationService;

    @Autowired
    public DataInitializationController(DataInitializationService dataInitializationService) {
        this.dataInitializationService = dataInitializationService;
    }

    @PostMapping("/initializeData")
    public ResponseEntity<String> initializeData() {
        boolean success = dataInitializationService.initializeDataForNewYear();

        if (success) {
            return ResponseEntity.ok("데이터 초기화가 완료되었습니다.");
        } else {
            return ResponseEntity.status(500).body("데이터 초기화 중 오류가 발생했습니다.");
        }
    }
}

