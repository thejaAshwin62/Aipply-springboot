package com.aipply.aipply.service;

import com.aipply.aipply.model.Application;
import com.aipply.aipply.projection.ApplicationStatus;
import com.aipply.aipply.repository.ApplicationRepository;
import com.aipply.aipply.repository.JobRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final ApplicationRepository applicationRepository;
    private final JobRepo jobRepo;

    public Map<String, Object> getUserStats(int userId) {
        List<Application> userApplications = applicationRepository.findByUserId(userId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalApplications", userApplications.size());
        
        long shortlisted = userApplications.stream()
            .filter(app -> app.getStatus() == ApplicationStatus.VALIDATING)
            .count();
        
        long pending = userApplications.stream()
            .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
            .count();
        
        long rejected = userApplications.stream()
            .filter(app -> app.getStatus() == ApplicationStatus.REJECTED)
            .count();

        stats.put("shortlisted", shortlisted);
        stats.put("pending", pending);
        stats.put("rejected", rejected);

        return stats;
    }

    public Map<String, Object> getCompanyStats(int companyId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Get active jobs count
        long activeJobs = jobRepo.countByCompanyId(companyId);
        stats.put("activeJobs", activeJobs);

        // Get total applicants for all company jobs
        List<Application> allApplications = applicationRepository.findByJobCompanyId(companyId);
        stats.put("totalApplicants", allApplications.size());

        // Get applications by status
        Map<ApplicationStatus, Long> applicationsByStatus = new HashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            long count = allApplications.stream()
                .filter(app -> app.getStatus() == status)
                .count();
            applicationsByStatus.put(status, count);
        }
        stats.put("applicationsByStatus", applicationsByStatus);

        return stats;
    }
}
