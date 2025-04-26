package com.aipply.aipply.service;

import com.aipply.aipply.model.UserResponse;
import com.aipply.aipply.repository.CompanyRepository;
import com.aipply.aipply.repository.JobRepo;
import com.aipply.aipply.repository.UserRepo;
import com.aipply.aipply.model.Job;
import com.aipply.aipply.model.Company;
import com.aipply.aipply.model.User;
import com.aipply.aipply.projection.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final UserRepo userRepo;
    private final CompanyRepository companyRepository;
    private final JobRepo jobRepo;
    
    public Map<String, Long> getAllStats() {
        Map<String, Long> stats = new HashMap<>();
        
        stats.put("totalUsers", userRepo.count());
        stats.put("totalCompanies", companyRepository.count());
        stats.put("totalJobs", jobRepo.count());
        stats.put("usersRequestingCompany", userRepo.countByRequestForCompanyTrue());
        
        return stats;
    }
    
    public List<UserResponse> getAllUsers() {
        return userRepo.findAll().stream()
            .map(user -> UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .companyId(user.getCompanyId())
                .yearsOfExperience(user.getYearsOfExperience())
                .role(user.getRoles().toString())
                .requestForCompany(user.getRequestForCompany())
                .createdAt(user.getCreatedAt())
                .build())
            .collect(Collectors.toList());
    }

    public List<Job> getAllJobs() {
        return jobRepo.findAll();
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public UserResponse changeUserRole(Integer userId, Roles newRole) {
        User user = userRepo.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        
        user.setRoles(newRole);
        User savedUser = userRepo.save(user);
        
        return UserResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .companyId(savedUser.getCompanyId())
                .yearsOfExperience(savedUser.getYearsOfExperience())
                .role(savedUser.getRoles().toString())
                .requestForCompany(savedUser.getRequestForCompany())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    public List<Map<String, Object>> getTopCompaniesByJobPostings() {
        return companyRepository.findAll().stream()
            .map(company -> {
                long jobCount = jobRepo.countByCompanyId(company.getId());
                Map<String, Object> companyStats = new HashMap<>();
                companyStats.put("companyName", company.getCompanyName());
                companyStats.put("jobCount", jobCount);
                return companyStats;
            })
            .sorted((c1, c2) -> Long.compare((Long) c2.get("jobCount"), (Long) c1.get("jobCount")))
            .limit(3)
            .collect(Collectors.toList());
    }
}
