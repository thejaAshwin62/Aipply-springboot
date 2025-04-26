package com.aipply.aipply.repository;

import com.aipply.aipply.model.Job;
import com.aipply.aipply.projection.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepo extends JpaRepository<Job, Integer> {
    List<Job> findByCompanyId(Integer companyId);
    
    List<Job> findByJobType(JobType jobType);
    
    List<Job> findByLocation(String location);
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Integer companyId);
    
    @Query("SELECT j FROM Job j WHERE j.salary BETWEEN :minSalary AND :maxSalary")
    List<Job> findBySalaryRange(@Param("minSalary") Double minSalary, @Param("maxSalary") Double maxSalary);
    
    List<Job> findByMockInterviewRequiredTrue();
    
    @Query("SELECT j FROM Job j WHERE j.mockInterviewRequired = true AND j.minimumMockScore <= :score")
    List<Job> findJobsByMockScore(@Param("score") Integer score);
}
