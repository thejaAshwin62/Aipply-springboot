package com.aipply.aipply.service;

import com.aipply.aipply.model.Application;
import com.aipply.aipply.model.Company;
import com.aipply.aipply.model.Job;
import com.aipply.aipply.model.User;
import com.aipply.aipply.projection.JobType;
import com.aipply.aipply.repository.ApplicationRepo;
import com.aipply.aipply.repository.JobRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepo repo;
    private final ApplicationRepo applicationRepo;
    private final CompanyService companyService;

    public Job createJob(Job job) {
        return repo.save(job);
    }

    public List<Job> getAllJobs() {
        return repo.findAll();
    }

    public Job getJobById(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }

    public List<Job> getJobsByCompany(Integer companyId) {
        return repo.findByCompanyId(companyId);
    }

    public List<User> getApplicantsForJob(int jobId) {
        return repo.findById(jobId)
                .map(job -> applicationRepo.findByJob(job)
                        .stream()
                        .map(application -> application.getUser())
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));
    }

    public Job updateJob(Integer id, Job job) {
        return repo.findById(id).map(existingJob -> {
            existingJob.setTitle(job.getTitle());
            existingJob.setTechStack(job.getTechStack());
            existingJob.setDescription(job.getDescription());
            existingJob.setCompany(job.getCompany());
            existingJob.setYearsOfExperience(job.getYearsOfExperience());
            existingJob.setJobType(job.getJobType());
            existingJob.setLocation(job.getLocation());
            existingJob.setSalary(job.getSalary());
            existingJob.setMockInterviewRequired(job.getMockInterviewRequired());
            existingJob.setMinimumMockScore(job.getMinimumMockScore());
            existingJob.setMockInterviewPercentage(job.getMockInterviewPercentage());
            return repo.save(existingJob);
        }).orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }

    public void deleteJob(Integer id) {
        repo.deleteById(id);
    }

    public List<Job> getJobsByType(JobType jobType) {
        return repo.findByJobType(jobType);
    }

    public List<Job> getJobsByLocation(String location) {
        return repo.findByLocation(location);
    }

    public List<Job> getJobsBySalaryRange(Double minSalary, Double maxSalary) {
        return repo.findBySalaryRange(minSalary, maxSalary);
    }

    public List<Job> getJobsRequiringMockInterview() {
        return repo.findByMockInterviewRequiredTrue();
    }
}
