package com.aipply.aipply.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private int id;
    private String email;
    private String name;
    private Integer companyId;
    private Integer yearsOfExperience;
    private Set<Long> appliedJobIds; // Set of job IDs that the user has applied to
    private String role;
    private Boolean requestForCompany;
    private LocalDateTime createdAt;
} 

