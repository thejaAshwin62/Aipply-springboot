package com.aipply.aipply.service;

import com.aipply.aipply.model.Job;
import com.aipply.aipply.model.MockQuestions;
import com.aipply.aipply.model.User;
import com.aipply.aipply.repository.MockQuestionsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MockQuestionService {

    private final MockQuestionsRepository mockQuestionsRepository;
    private final JobService jobService;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MockQuestions getQuestionById(int questionId) {
        if (questionId <= 0) {
            throw new IllegalArgumentException("Invalid question ID");
        }
        return mockQuestionsRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + questionId));
    }

    @Transactional
    public List<MockQuestions> generateMockQuestions(int jobId, User user) {
        if (jobId <= 0) {
            throw new IllegalArgumentException("Invalid job ID");
        }
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        Job job = jobService.getJobById(jobId);
        if (job == null) {
            throw new IllegalArgumentException("Job not found with id: " + jobId);
        }
        
        // Prepare request to Node.js server
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
            "techStack", job.getTechStack(),
            "jobDescription", job.getDescription(),
            "yearsOfExperience", job.getYearsOfExperience()
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        List<MockQuestions> questions = new ArrayList<>();
        try {
            // Call Node.js server
//            ResponseEntity<String> response = restTemplate.postForEntity(
//                "http://localhost:3001/generate-questions",
//                request,
//                String.class
//            );
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://mockai-service.onrender.com/generate-questions",
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the response
                Map<String, Object> responseBody = objectMapper.readValue(
                    response.getBody(),
                    Map.class
                );

                if (Boolean.TRUE.equals(responseBody.get("success"))) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> qaList = (List<Map<String, String>>) responseBody.get("data");
                    
                    for (Map<String, String> qa : qaList) {
                        String question = qa.get("question");
                        String answer = qa.get("answer");

                        // Validate question and answer
                        if (question == null || question.trim().isEmpty() || 
                            answer == null || answer.trim().isEmpty()) {
                            continue; // Skip invalid entries
                        }

                        MockQuestions mockQuestion = new MockQuestions();
                        mockQuestion.setQuestion(question.trim());
                        mockQuestion.setAnswer(answer.trim());
                        mockQuestion.setJob(job);
                        mockQuestion.setUser(user);
                        questions.add(mockQuestionsRepository.save(mockQuestion));
                    }

                    if (questions.isEmpty()) {
                        throw new RuntimeException("No valid questions were generated");
                    }
                } else {
                    throw new RuntimeException("Failed to generate questions: " + responseBody.get("error"));
                }
            } else {
                throw new RuntimeException("Failed to generate questions from Node.js server");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate mock questions: " + e.getMessage());
        }

        return questions;
    }

    public List<MockQuestions> getQuestionsByJob(int jobId) {
        if (jobId <= 0) {
            throw new IllegalArgumentException("Invalid job ID");
        }
        Job job = jobService.getJobById(jobId);
        if (job == null) {
            throw new IllegalArgumentException("Job not found with id: " + jobId);
        }
        return mockQuestionsRepository.findByJobId(jobId);
    }

    public List<MockQuestions> getQuestionsByUser(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        return mockQuestionsRepository.findByUserId(userId);
    }

    public List<MockQuestions> getQuestionsByJobAndUser(int jobId, int userId) {
        if (jobId <= 0) {
            throw new IllegalArgumentException("Invalid job ID");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        Job job = jobService.getJobById(jobId);
        if (job == null) {
            throw new IllegalArgumentException("Job not found with id: " + jobId);
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }

        List<MockQuestions> questions = mockQuestionsRepository.findByJobIdAndUserId(jobId, userId);
        if (questions.isEmpty()) {
            throw new RuntimeException("No questions found for job ID: " + jobId + " and user ID: " + userId);
        }

        return questions;
    }
}

