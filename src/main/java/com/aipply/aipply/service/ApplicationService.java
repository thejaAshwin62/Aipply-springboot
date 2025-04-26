package com.aipply.aipply.service;

import com.aipply.aipply.model.Application;

import com.aipply.aipply.model.Job;
import com.aipply.aipply.model.User;
import com.aipply.aipply.projection.ApplicationStatus;
import com.aipply.aipply.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobService jobService;
    private final UserService userService;
    private final FeedbackService feedbackService;

    @Transactional
    public Application applyForJob(int jobId, int userId) {
        Job job = jobService.getJobById(jobId);
        User user = userService.getUserById(userId);

        // Check if user has already applied for this job
        if (applicationRepository.existsByJobAndUser(job, user)) {
            throw new RuntimeException("User has already applied for this job");
        }

        Application application = new Application();
        application.setJob(job);
        application.setUser(user);
        application.setStatus(ApplicationStatus.PENDING);

        return applicationRepository.save(application);
    }

    @Transactional
    public Application updateApplicationStatus(int applicationId, ApplicationStatus newStatus, String notes) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(newStatus);
        if (notes != null) {
            application.setNotes(notes);
        }

        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsByJob(int jobId) {
        Job job = jobService.getJobById(jobId);
        return applicationRepository.findByJob(job);
    }

    public List<Map<String, Object>> getDetailedApplicationsByJob(int jobId) {
        Job job = jobService.getJobById(jobId);
        List<Application> applications = applicationRepository.findByJob(job);
        
        return applications.stream()
            .map(application -> {
                Map<String, Object> applicationDetails = new HashMap<>();
                User user = application.getUser();
                
                applicationDetails.put("applicationId", application.getId());
                applicationDetails.put("userId", user.getId());
                applicationDetails.put("userName", user.getName());
                applicationDetails.put("userEmail", user.getEmail());
                applicationDetails.put("yearsOfExperience", user.getYearsOfExperience());
                applicationDetails.put("status", application.getStatus());
                applicationDetails.put("appliedAt", application.getAppliedAt());
                applicationDetails.put("notes", application.getNotes());
                
                // Add interview score if available
                int interviewScore = feedbackService.calculateTotalRating(user.getId().longValue(), job.getId().longValue());
                applicationDetails.put("interviewScore", interviewScore);
                
                return applicationDetails;
            })
            .collect(Collectors.toList());
    }

    public List<Application> getApplicationsByUser(int userId) {
        User user = userService.getUserById(userId);
        return applicationRepository.findByUser(user);
    }

    public Application getApplicationById(int applicationId) {
        return applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    public Map<String, Object> getJobApplicationStats(int jobId) {
        Job job = jobService.getJobById(jobId);
        List<Application> applications = applicationRepository.findByJob(job);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalApplicants", applications.size());
        stats.put("pending",applications.stream()
                .filter(app-> app.getStatus() == ApplicationStatus.PENDING)
                .count());
        stats.put("approved", applications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.APPROVED)
                .count());
        stats.put("validating", applications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.VALIDATING)
                .count());
        stats.put("rejected", applications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.REJECTED)
                .count());
        
        return stats;
    }

    public List<Map<String, Object>> getCompanyApplications(int companyId) {
        List<Job> companyJobs = jobService.getJobsByCompany(companyId);
        List<Application> allApplications = applicationRepository.findByJobIn(companyJobs);
        
        return allApplications.stream()
            .map(application -> {
                Map<String, Object> applicationDetails = new HashMap<>();
                User user = application.getUser();
                Job job = application.getJob();
                
                applicationDetails.put("applicationId", application.getId());
                applicationDetails.put("userId", user.getId());
                applicationDetails.put("userName", user.getName());
                applicationDetails.put("userEmail", user.getEmail());
                applicationDetails.put("jobId", job.getId());
                applicationDetails.put("jobTitle", job.getTitle());
                applicationDetails.put("jobType", job.getJobType());
                applicationDetails.put("location", job.getLocation());
                applicationDetails.put("salary", job.getSalary());
                applicationDetails.put("status", application.getStatus());
                applicationDetails.put("appliedAt", application.getAppliedAt());
                applicationDetails.put("notes", application.getNotes());
                
                return applicationDetails;
            })
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getUserCompanyApplications(int userId, int companyId) {
        List<Job> companyJobs = jobService.getJobsByCompany(companyId);
        List<Application> userApplications = applicationRepository.findByUserIdAndJobIn(userId, companyJobs);
        
        return userApplications.stream()
            .map(application -> {
                Map<String, Object> applicationDetails = new HashMap<>();
                Job job = application.getJob();
                
                applicationDetails.put("applicationId", application.getId());
                applicationDetails.put("jobId", job.getId());
                applicationDetails.put("jobTitle", job.getTitle());
                applicationDetails.put("jobType", job.getJobType());
                applicationDetails.put("location", job.getLocation());
                applicationDetails.put("salary", job.getSalary());
                applicationDetails.put("status", application.getStatus());
                applicationDetails.put("appliedAt", application.getAppliedAt());
                applicationDetails.put("notes", application.getNotes());
                
                return applicationDetails;
            })
            .collect(Collectors.toList());
    }
    
    public List<Map<String, Object>> getCompanyApplicantsDetails(int companyId) {
        List<Job> companyJobs = jobService.getJobsByCompany(companyId);
        List<Application> allApplications = applicationRepository.findByJobIn(companyJobs);
        
        return allApplications.stream()
            .map(application -> {
                Map<String, Object> applicantDetails = new HashMap<>();
                User user = application.getUser();
                Job job = application.getJob();
                
                // Applicant details
                applicantDetails.put("applicantId", user.getId());
                applicantDetails.put("applicantName", user.getName());
                applicantDetails.put("applicantEmail", user.getEmail());
                
                // Job details
                applicantDetails.put("jobId", job.getId());
                applicantDetails.put("jobTitle", job.getTitle());
                
                // Application details
                applicantDetails.put("applicationId", application.getId());
                applicantDetails.put("appliedDate", application.getAppliedAt());
                applicantDetails.put("status", application.getStatus());
                
                // Interview details with feedback rating
                int interviewScore = feedbackService.calculateTotalRating(user.getId().longValue(), job.getId().longValue());
                applicantDetails.put("interviewScore", interviewScore);
                applicantDetails.put("mockInterviewRequired", job.getMockInterviewRequired());
                applicantDetails.put("minimumMockScore", job.getMinimumMockScore());
                
                return applicantDetails;
            })
            .collect(Collectors.toList());
    }
} 