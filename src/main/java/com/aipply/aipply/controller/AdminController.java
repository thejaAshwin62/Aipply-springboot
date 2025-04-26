package com.aipply.aipply.controller;

import com.aipply.aipply.model.Company;
import com.aipply.aipply.model.Job;
import com.aipply.aipply.model.UpdateUser;
import com.aipply.aipply.model.UserResponse;
import com.aipply.aipply.projection.Roles;
import com.aipply.aipply.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {


    private final AdminService adminService;

    /**
     * Get all admin statistics in a single endpoint
     * @return a map containing counts of users, companies, jobs, and users requesting to join companies
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getAllStats() {
        return ResponseEntity.ok(adminService.getAllStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(adminService.getAllJobs());
    }

    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(adminService.getAllCompanies());
    }

    /**
     * Get the top 3 companies with the most job postings
     * @return a list of companies ordered by job postings count
     */
    @GetMapping("/top-companies")
    public ResponseEntity<List<Map<String, Object>>> getTopCompaniesByJobPostings() {
        return ResponseEntity.ok(adminService.getTopCompaniesByJobPostings());
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserResponse> changeUserRole(
            @PathVariable Integer userId,
            @RequestBody UpdateUser updateUser) {
        if (updateUser.getRole() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(adminService.changeUserRole(userId, Roles.valueOf(updateUser.getRole())));
    }
}

