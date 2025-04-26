package com.aipply.aipply.controller;

import com.aipply.aipply.model.Company;
import com.aipply.aipply.model.Job;
import com.aipply.aipply.model.User;
import com.aipply.aipply.service.CompanyService;
import com.aipply.aipply.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {


    private final CompanyService companyService;
    private final UserService userService;

    @PreAuthorize("hasRole('Company')")
    @PostMapping
    public ResponseEntity<Company> registerCompany(@RequestBody Company company) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        
        // Register the company
        Company registeredCompany = companyService.registerCompany(company);
        
        // Update the user with the company ID
        User user = userService.getCurrentUser(userEmail);
        user.setCompanyId(registeredCompany.getId());
        userService.updateUserCompanyId(user.getId(), registeredCompany.getId());
        
        return ResponseEntity.ok(registeredCompany);
    }

    @PreAuthorize("hasAnyRole('User', 'Company', 'Admin')")
    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Integer id) {
        return companyService.getCompanyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('Company')")
    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable Integer id, @RequestBody Company company) {
        Company updatedCompany = companyService.updateCompany(id, company);
        return ResponseEntity.ok(updatedCompany);
    }

    @PreAuthorize("hasRole('Company')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Integer id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('User', 'Company', 'Admin')")
    @GetMapping("/{companyId}/jobs")
    public ResponseEntity<List<Job>> getAllJobsByCompany(@PathVariable Integer companyId) {
        List<Job> jobs = companyService.getAllJobsByCompany(companyId);
        return ResponseEntity.ok(jobs);
    }

    @PreAuthorize("hasAnyRole('User', 'Company', 'Admin')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Company> getCompanyByUserId(@PathVariable Integer userId) {
        return companyService.getCompanyByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 