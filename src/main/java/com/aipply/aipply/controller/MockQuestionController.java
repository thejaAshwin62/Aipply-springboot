package com.aipply.aipply.controller;

import com.aipply.aipply.model.MockQuestions;
import com.aipply.aipply.model.User;
import com.aipply.aipply.service.MockQuestionService;
import com.aipply.aipply.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mock-questions")
@RequiredArgsConstructor
public class MockQuestionController {

    private final MockQuestionService mockQuestionService;
    private final UserService userService;

    @GetMapping("/{questionId}")
    public ResponseEntity<MockQuestions> getQuestionById(@PathVariable int questionId) {
        try {
            MockQuestions question = mockQuestionService.getQuestionById(questionId);
            return ResponseEntity.ok(question);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/generate/job/{jobId}/user/{userId}")
    public ResponseEntity<List<MockQuestions>> generateQuestions(
            @PathVariable int jobId,
            @PathVariable int userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            List<MockQuestions> questions = mockQuestionService.generateMockQuestions(jobId, user);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<MockQuestions>> getQuestionsByJob(@PathVariable int jobId) {
        List<MockQuestions> questions = mockQuestionService.getQuestionsByJob(jobId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MockQuestions>> getQuestionsByUser(@PathVariable int userId) {
        try {
            List<MockQuestions> questions = mockQuestionService.getQuestionsByUser(userId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/job/{jobId}/user/{userId}")
    public ResponseEntity<List<MockQuestions>> getQuestionsByJobAndUser(
            @PathVariable int jobId,
            @PathVariable int userId) {
        try {
            List<MockQuestions> questions = mockQuestionService.getQuestionsByJobAndUser(jobId, userId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
