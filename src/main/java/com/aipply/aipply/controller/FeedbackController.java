package com.aipply.aipply.controller;

import com.aipply.aipply.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/user/{userId}/job/{jobId}")
    public ResponseEntity<List<Map<String, Object>>> getLatestFeedbackByUserAndJob(
            @PathVariable Long userId,
            @PathVariable Long jobId) {
        try {
            List<Map<String, Object>> feedback = feedbackService.getLatestFeedbackByUserAndJob(userId, jobId);
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getLatestFeedbackByUser(
            @PathVariable Long userId) {
        try {
            List<Map<String, Object>> feedback = feedbackService.getLatestFeedbackByUser(userId);
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
