package com.aipply.aipply.service;

import com.aipply.aipply.model.Company;
import com.aipply.aipply.model.Job;
import com.aipply.aipply.model.User;
import com.aipply.aipply.repository.CompanyRepository;
import com.aipply.aipply.repository.JobRepo;
import com.aipply.aipply.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {


    private final CompanyRepository companyRepository;
    private final JobRepo jobRepo;
    private final UserRepo userRepo;

    @Override
    public Company registerCompany(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public Optional<Company> getCompanyById(Integer id) {
        return companyRepository.findById(id);
    }

    @Override
    public Company updateCompany(Integer id, Company company) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        
        existingCompany.setCompanyName(company.getCompanyName());
        existingCompany.setCompanyEmail(company.getCompanyEmail());
        existingCompany.setDescription(company.getDescription());
        existingCompany.setWebsite(company.getWebsite());
        
        return companyRepository.save(existingCompany);
    }

    @Override
    public void deleteCompany(Integer id) {
        companyRepository.deleteById(id);
    }

    @Override
    public List<Job> getAllJobsByCompany(Integer companyId) {
        return jobRepo.findByCompanyId(companyId);
    }
    
    @Override
    public Optional<Company> getCompanyByUserId(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getCompanyId() == null) {
            return Optional.empty();
        }
        
        return companyRepository.findById(user.getCompanyId());
    }
} 