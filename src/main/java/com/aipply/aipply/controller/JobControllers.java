package com.aipply.aipply.controller;

import com.aipply.aipply.model.Job;
import com.aipply.aipply.model.User;
import com.aipply.aipply.projection.JobType;
import com.aipply.aipply.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobControllers {

    private final JobService service;

    @PreAuthorize("hasRole('Company')")
    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        Job savedJob = service.createJob(job);
        return ResponseEntity.status(201).body(savedJob);
    }

    @PreAuthorize("hasAnyRole('User', 'Company', 'Admin')")
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> allJobs = service.getAllJobs();
        return ResponseEntity.ok(allJobs);
    }

    @PreAuthorize("hasAnyRole('User', 'Company', 'Admin')")
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Integer id) {
        Job job = service.getJobById(id);
        return ResponseEntity.ok(job);
    }

    @PreAuthorize("hasAnyRole('User', 'Company', 'Admin')")
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Job>> getJobsByCompany(@PathVariable Integer companyId) {
        List<Job> jobs = service.getJobsByCompany(companyId);
        return ResponseEntity.ok(jobs);
    }

    @PreAuthorize("hasRole('Company')")
    @GetMapping("/{jobId}/applicants")
    public ResponseEntity<List<User>> getApplicantsForJob(@PathVariable int jobId) {
        List<User> applicants = service.getApplicantsForJob(jobId);
        return ResponseEntity.ok(applicants);
    }

    @PreAuthorize("hasAnyRole('User', 'Company', 'Admin')")
    @GetMapping("/type/{jobType}")
    public ResponseEntity<List<Job>> getJobsByType(@PathVariable JobType jobType) {
        List<Job> jobs = service.getJobsByType(jobType);
        return ResponseEntity.ok(jobs);
    }

    @PreAuthorize("hasAnyRole('User', 'Company', 'Admin')")
    @GetMapping("/location/{location}")
    public ResponseEntity<List<Job>> getJobsByLocation(@PathVariable String location) {
        List<Job> jobs = service.getJobsByLocation(location);
        return ResponseEntity.ok(jobs);
    }

    @PreAuthorize("hasAnyRole('User', 'Company', 'Admin')")
    @GetMapping("/salary")
    public ResponseEntity<List<Job>> getJobsBySalaryRange(
            @RequestParam Double minSalary,
            @RequestParam Double maxSalary) {
        List<Job> jobs = service.getJobsBySalaryRange(minSalary, maxSalary);
        return ResponseEntity.ok(jobs);
    }

    @PreAuthorize("hasAnyRole('User', 'Company', 'Admin')")
    @GetMapping("/mock-interview")
    public ResponseEntity<List<Job>> getJobsRequiringMockInterview() {
        List<Job> jobs = service.getJobsRequiringMockInterview();
        return ResponseEntity.ok(jobs);
    }

    @PreAuthorize("hasRole('Company')")
    @PutMapping("/{jobId}")
    public ResponseEntity<Job> updateJob(@PathVariable Integer jobId, @RequestBody Job job) {
        try {
            // Get the existing job to preserve the company
            Job existingJob = service.getJobById(jobId);
            if (existingJob == null) {
                return ResponseEntity.notFound().build();
            }

            // Preserve the company from the existing job
            job.setCompany(existingJob.getCompany());

            // If techStack is a single string with commas, split it into an array
            if (job.getTechStack() != null && job.getTechStack().size() == 1) {
                String techStackStr = job.getTechStack().get(0);
                if (techStackStr.contains(",")) {
                    job.setTechStack(Arrays.asList(techStackStr.split(",")));
                }
            }

            Job updatedJob = service.updateJob(jobId, job);
            return ResponseEntity.ok(updatedJob);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('Company')")
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Integer jobId) {
        service.deleteJob(jobId);
        return ResponseEntity.ok().build();
    }
}
