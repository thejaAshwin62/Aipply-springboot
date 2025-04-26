package com.aipply.aipply.controller;

import com.aipply.aipply.model.Application;

import com.aipply.aipply.projection.ApplicationStatus;
import com.aipply.aipply.service.ApplicationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @Data
    public static class ApplyRequest {
        private int jobId;
        private int userId;
    }

    @Data
    public static class UpdateStatusRequest {
        private ApplicationStatus status;
        private String notes;
    }

    @PreAuthorize("hasRole('User')")
    @PostMapping("/apply")
    public ResponseEntity<Application> applyForJob(@RequestBody ApplyRequest request) {
        try {
            Application application = applicationService.applyForJob(request.getJobId(), request.getUserId());
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('Company')")
    @PutMapping("/{applicationId}/status")
    public ResponseEntity<Application> updateApplicationStatus(
            @PathVariable int applicationId,
            @RequestBody UpdateStatusRequest request) {
        try {
            Application application = applicationService.updateApplicationStatus(
                applicationId,
                request.getStatus(),
                request.getNotes()
            );
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('Company')")
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getApplicationsByJob(@PathVariable int jobId) {
        try {
            List<Application> applications = applicationService.getApplicationsByJob(jobId);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('User')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Application>> getApplicationsByUser(@PathVariable int userId) {
        try {
            List<Application> applications = applicationService.getApplicationsByUser(userId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('User', 'Company')")
    @GetMapping("/{applicationId}")
    public ResponseEntity<Application> getApplicationById(@PathVariable int applicationId) {
        try {
            Application application = applicationService.getApplicationById(applicationId);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('Company')")
    @GetMapping("/job/{jobId}/stats")
    public ResponseEntity<Map<String, Object>> getJobApplicationStats(@PathVariable int jobId) {
        try {
            Map<String, Object> stats = applicationService.getJobApplicationStats(jobId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('Company')")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Map<String, Object>>> getCompanyApplications(@PathVariable int companyId) {
        try {
            List<Map<String, Object>> applications = applicationService.getCompanyApplications(companyId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('User', 'Company')")
    @GetMapping("/user/{userId}/company/{companyId}")
    public ResponseEntity<List<Map<String, Object>>> getUserCompanyApplications(
            @PathVariable int userId,
            @PathVariable int companyId) {
        try {
            List<Map<String, Object>> applications = applicationService.getUserCompanyApplications(userId, companyId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('Company')")
    @GetMapping("/company/{companyId}/applicants")
    public ResponseEntity<List<Map<String, Object>>> getCompanyApplicantsDetails(@PathVariable int companyId) {
        try {
            List<Map<String, Object>> applicants = applicationService.getCompanyApplicantsDetails(companyId);
            return ResponseEntity.ok(applicants);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/job/{jobId}/detailed")
    public ResponseEntity<List<Map<String, Object>>> getDetailedApplicationsByJob(@PathVariable int jobId) {
        try {
            List<Map<String, Object>> applications = applicationService.getDetailedApplicationsByJob(jobId);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 