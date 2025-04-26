package com.aipply.aipply.controller;

import com.aipply.aipply.model.UserAnswer;
import com.aipply.aipply.service.UserAnswerService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-answers")
@RequiredArgsConstructor
public class UserAnswerController {

    private final UserAnswerService userAnswerService;

    @Data
    public static class SubmitAnswerRequest {
        private int mockQuestionId;
        private int userId;
        private String userAnswer;
    }

    @PostMapping("/submit")
    public ResponseEntity<UserAnswer> submitAnswer(@RequestBody SubmitAnswerRequest request) {
        try {
            UserAnswer answer = userAnswerService.submitAnswer(
                request.getMockQuestionId(),
                request.getUserId(),
                request.getUserAnswer()
            );
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{userAnswerId}")
    public ResponseEntity<UserAnswer> getUserAnswer(@PathVariable int userAnswerId) {
        try {
            UserAnswer answer = userAnswerService.getUserAnswer(userAnswerId);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 