package com.aipply.aipply.service;

import com.aipply.aipply.model.*;
import com.aipply.aipply.projection.Roles;
import com.aipply.aipply.repository.ApplicationRepository;
import com.aipply.aipply.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo repo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApplicationRepository applicationRepository;

    @Autowired
    private UserRepo userRepo;

    public User saveData(User user){
        if (repo.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("User with this email already exists");
        }
        if (repo.findByName(user.getName()) != null) {
            throw new RuntimeException("User with this name already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if(user.getRoles()==null){
            user.setRoles(Roles.User);
        }
        return repo.save(user);
    }

    public User getCurrentUser(String email){
        User user = repo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }

    public User updateUser(int id, UpdateUser request){
        Optional<User> optionalUser = repo.findById(id);
        if(optionalUser.isEmpty()){
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();
        if(request.getName() != null) {
            // Check if new name is already taken by another user
            User existingUser = repo.findByName(request.getName());
            if (existingUser != null && existingUser.getId() != id) {
                throw new RuntimeException("Name is already taken");
            }
            user.setName(request.getName());
        }
        if(request.getEmail()!=null) user.setEmail(request.getEmail());
        if(request.getYearsOfExperience()!=null) user.setYearsOfExperience(request.getYearsOfExperience());
        return repo.save(user);
    }

    public User updateUserCompanyId(int userId, Integer companyId) {
        Optional<User> optionalUser = repo.findById(userId);
        if(optionalUser.isEmpty()){
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();
        user.setCompanyId(companyId);
        return repo.save(user);
    }

    public User getUserById(int userId){
        User user =  repo.findById(userId).get();
        return user;
    }

    public UserResponse getCurrentUserWithAppliedJobs(String email) {
        User user = getCurrentUser(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Get all job IDs that the user has applied to
        Set<Integer> appliedJobIds = applicationRepository.findByUserId(user.getId())
                .stream()
                .map(application -> application.getJob().getId())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .companyId(user.getCompanyId())
                .yearsOfExperience(user.getYearsOfExperience())
                .appliedJobIds(appliedJobIds.stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toSet()))
                .role(user.getRoles() != null ? user.getRoles().name() : null)
                .build();
    }

    public List<Map<String, Object>> getUserAppliedJobs(int userId) {
        List<Application> applications = applicationRepository.findByUserId(userId);
        
        return applications.stream()
            .map(application -> {
                Map<String, Object> jobDetails = new HashMap<>();
                Job job = application.getJob();
                
                jobDetails.put("jobId", job.getId());
                jobDetails.put("title", job.getTitle());
                jobDetails.put("company", job.getCompany().getCompanyName());
                jobDetails.put("salary", job.getSalary());
                jobDetails.put("location", job.getLocation());
                jobDetails.put("jobType", job.getJobType());
                jobDetails.put("applicationStatus", application.getStatus());
                jobDetails.put("appliedDate", application.getAppliedAt());
                jobDetails.put("mockInterviewRequired", job.getMockInterviewRequired());
                jobDetails.put("minimumMockScore", job.getMinimumMockScore());
                
                return jobDetails;
            })
            .collect(Collectors.toList());
    }

}
