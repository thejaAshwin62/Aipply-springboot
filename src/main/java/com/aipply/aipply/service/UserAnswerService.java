package com.aipply.aipply.service;

import com.aipply.aipply.model.MockQuestions;
import com.aipply.aipply.model.User;
import com.aipply.aipply.model.UserAnswer;
import com.aipply.aipply.repository.UserAnswerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserAnswerService {

    private final UserAnswerRepository userAnswerRepository;
    private final MockQuestionService mockQuestionService;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public UserAnswer submitAnswer(int mockQuestionId, int userId, String userAnswer) {
        MockQuestions mockQuestion = mockQuestionService.getQuestionById(mockQuestionId);
        if (mockQuestion == null) {
            throw new IllegalArgumentException("Mock question not found");
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Create and save the answer first
        UserAnswer answer = new UserAnswer();
        answer.setMockQuestions(mockQuestion);
        answer.setUser(user);
        answer.setUserAnswer(userAnswer);
        answer.setEvaluated(false);
        answer = userAnswerRepository.save(answer);

        // Immediately evaluate the answer
        try {
            // Prepare request to Node.js server
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = Map.of(
                "question", mockQuestion.getQuestion(),
                "correctAnswer", mockQuestion.getAnswer(),
                "userAnswer", userAnswer
            );

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // Call Node.js server
//            ResponseEntity<String> response = restTemplate.postForEntity(
//                "http://localhost:3001/generate-feedback",
//                request,
//                String.class
//            );

            ResponseEntity<String> response = restTemplate.postForEntity(
                "https://mockai-service.onrender.com/generate-feedback",
                request,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = objectMapper.readValue(
                    response.getBody(),
                    Map.class
                );

                if (Boolean.TRUE.equals(responseBody.get("success"))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> feedbackData = (Map<String, Object>) responseBody.get("data");
                    
                    answer.setFeedback((String) feedbackData.get("feedback"));
                    // Ensure rating is between 1 and 5
                    int rating = ((Number) feedbackData.get("rating")).intValue();
                    answer.setRating(Math.min(5, Math.max(1, rating)));
                    answer.setEvaluated(true);
                    
                    return userAnswerRepository.save(answer);
                }
            }
            throw new RuntimeException("Failed to generate feedback");
        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate answer: " + e.getMessage());
        }
    }

    public UserAnswer getUserAnswer(int userAnswerId) {
        return userAnswerRepository.findById(userAnswerId)
            .orElseThrow(() -> new IllegalArgumentException("User answer not found"));
    }
} 