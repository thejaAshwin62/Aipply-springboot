package com.aipply.aipply.repository;

import com.aipply.aipply.model.MockQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockQuestionsRepository extends JpaRepository<MockQuestions, Integer> {
    List<MockQuestions> findByJobId(int jobId);
    List<MockQuestions> findByUserId(int userId);
    List<MockQuestions> findByJobIdAndUserId(int jobId, int userId);
} 