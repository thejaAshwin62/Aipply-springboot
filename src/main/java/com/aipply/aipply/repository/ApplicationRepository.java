package com.aipply.aipply.repository;

import com.aipply.aipply.model.Application;
import com.aipply.aipply.model.Job;
import com.aipply.aipply.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByJob(Job job);
    List<Application> findByUser(User user);
    boolean existsByJobAndUser(Job job, User user);
    List<Application> findByUserId(int userId);
    
    @Query("SELECT a FROM Application a WHERE a.job.company.id = :companyId")
    List<Application> findByJobCompanyId(@Param("companyId") int companyId);

    List<Application> findByJobIn(List<Job> jobs);
    List<Application> findByUserIdAndJobIn(int userId, List<Job> jobs);
} 