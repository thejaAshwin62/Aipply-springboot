package com.aipply.aipply.repository;

import com.aipply.aipply.model.UserAnswer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Integer> {
    
    @Query("SELECT ua FROM UserAnswer ua WHERE ua.user.id = :userId AND ua.mockQuestions.job.id = :jobId ORDER BY ua.createdAt ASC")
    List<UserAnswer> findByUserIdAndJobIdOrderByCreatedAtDesc(
        @Param("userId") Long userId, 
        @Param("jobId") Long jobId, 
        Pageable pageable
    );

    @Query("SELECT ua FROM UserAnswer ua WHERE ua.user.id = :userId ORDER BY ua.createdAt ASC")
    List<UserAnswer> findByUserIdOrderByCreatedAtDesc(
        @Param("userId") Long userId, 
        Pageable pageable
    );
}
