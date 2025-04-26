package com.aipply.aipply.repository;

import com.aipply.aipply.model.Application;
import com.aipply.aipply.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepo extends JpaRepository<Application, Integer> {
    List<Application> findByJob(Job job);
}