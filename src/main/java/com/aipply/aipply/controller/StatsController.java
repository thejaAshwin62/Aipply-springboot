package com.aipply.aipply.controller;

import com.aipply.aipply.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable int userId) {
        return ResponseEntity.ok(statsService.getUserStats(userId));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<Map<String, Object>> getCompanyStats(@PathVariable int companyId) {
        return ResponseEntity.ok(statsService.getCompanyStats(companyId));
    }
}
