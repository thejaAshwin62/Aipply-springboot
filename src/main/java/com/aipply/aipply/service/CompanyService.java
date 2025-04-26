package com.aipply.aipply.service;

import com.aipply.aipply.model.Company;
import com.aipply.aipply.model.Job;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    Company registerCompany(Company company);
    Optional<Company> getCompanyById(Integer id);
    Company updateCompany(Integer id, Company company);
    void deleteCompany(Integer id);
    List<Job> getAllJobsByCompany(Integer companyId);
    Optional<Company> getCompanyByUserId(Integer userId);
}

