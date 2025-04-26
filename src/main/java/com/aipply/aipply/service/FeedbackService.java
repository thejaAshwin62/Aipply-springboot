package com.aipply.aipply.service;

import com.aipply.aipply.model.UserAnswer;
import com.aipply.aipply.model.MockQuestions;
import com.aipply.aipply.repository.UserAnswerRepository;
import com.aipply.aipply.repository.MockQuestionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackService {

    private final UserAnswerRepository userAnswerRepository;
    private final MockQuestionsRepository mockQuestionsRepository;

    public List<Map<String, Object>> getLatestFeedbackByUserAndJob(Long userId, Long jobId) {
        try {
            // Get the latest 5 user answers for the given user and job
            List<UserAnswer> userAnswers = userAnswerRepository.findByUserIdAndJobIdOrderByCreatedAtDesc(
                userId, 
                jobId, 
                PageRequest.of(0, 5, Sort.by("createdAt").ascending())
            );

            if (userAnswers.isEmpty()) {
                return Collections.emptyList();
            }

            // Get all mock questions for these answers
            List<Integer> questionIds = userAnswers.stream()
                .map(answer -> answer.getMockQuestions().getId())
                .collect(Collectors.toList());

            List<MockQuestions> questions = mockQuestionsRepository.findAllById(questionIds);

            // Map questions by ID for easy lookup
            Map<Integer, MockQuestions> questionMap = questions.stream()
                .collect(Collectors.toMap(MockQuestions::getId, q -> q));

            // Combine user answers with their corresponding questions
            return userAnswers.stream()
                .map(answer -> {
                    Map<String, Object> feedbackMap = new HashMap<>();
                    feedbackMap.put("id", answer.getId());
                    feedbackMap.put("question", questionMap.get(answer.getMockQuestions().getId()).getQuestion());
                    feedbackMap.put("userAnswer", answer.getUserAnswer());
                    feedbackMap.put("feedback", answer.getFeedback());
                    feedbackMap.put("rating", answer.getRating());
                    feedbackMap.put("createdAt", answer.getCreatedAt());
                    return feedbackMap;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Map<String, Object>> getLatestFeedbackByUser(Long userId) {
        try {
            // Get the latest 5 user answers for the given user
            List<UserAnswer> userAnswers = userAnswerRepository.findByUserIdOrderByCreatedAtDesc(
                userId, 
                PageRequest.of(0, 5, Sort.by("createdAt").ascending())
            );

            if (userAnswers.isEmpty()) {
                return Collections.emptyList();
            }

            // Get all mock questions for these answers
            List<Integer> questionIds = userAnswers.stream()
                .map(answer -> answer.getMockQuestions().getId())
                .collect(Collectors.toList());

            List<MockQuestions> questions = mockQuestionsRepository.findAllById(questionIds);

            // Map questions by ID for easy lookup
            Map<Integer, MockQuestions> questionMap = questions.stream()
                .collect(Collectors.toMap(MockQuestions::getId, q -> q));

            // Combine user answers with their corresponding questions
            return userAnswers.stream()
                .map(answer -> {
                    Map<String, Object> feedbackMap = new HashMap<>();
                    feedbackMap.put("id", answer.getId());
                    feedbackMap.put("question", questionMap.get(answer.getMockQuestions().getId()).getQuestion());
                    feedbackMap.put("userAnswer", answer.getUserAnswer());
                    feedbackMap.put("feedback", answer.getFeedback());
                    feedbackMap.put("rating", answer.getRating());
                    feedbackMap.put("createdAt", answer.getCreatedAt());
                    return feedbackMap;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public int calculateTotalRating(Long userId, Long jobId) {
        try {
            List<UserAnswer> userAnswers = userAnswerRepository.findByUserIdAndJobIdOrderByCreatedAtDesc(
                userId, 
                jobId, 
                PageRequest.of(0, 5, Sort.by("createdAt").ascending())
            );

            if (userAnswers.isEmpty()) {
                return 0;
            }

            // Calculate average rating
            double averageRating = userAnswers.stream()
                .mapToInt(UserAnswer::getRating)
                .average()
                .orElse(0.0);

            // Convert to percentage (0-100)
            return (int) Math.round(averageRating * 20); // Assuming rating is 1-5, multiply by 20 to get percentage
        } catch (Exception e) {
            return 0;
        }
    }
}
