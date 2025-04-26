package com.aipply.aipply.model;

import lombok.Data;

@Data
public class UpdateUser {

    private String name;
    private String email;
    private Integer yearsOfExperience;
    private String role;
}

