package com.aipply.aipply.config;

import com.aipply.aipply.model.Company;
import com.aipply.aipply.model.User;
import com.aipply.aipply.projection.Roles;
import com.aipply.aipply.repository.CompanyRepository;
import com.aipply.aipply.repository.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("user")
public class TestUserLoader {

    private static final Logger logger = LoggerFactory.getLogger(TestUserLoader.class);
    private final BCryptPasswordEncoder passwordEncoder;

    public TestUserLoader() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner loadTestData(
            CompanyRepository companyRepository,
            UserRepo userRepository) {
        return args -> {
            List<Company> companies = companyRepository.findAll();
            if (companies.isEmpty()) {
                logger.warn("No companies found in database - skipping user creation");
                return;
            }

            List<User> users = new ArrayList<>();
            for (Company company : companies) {
                // Start from company ID 8
                if (company.getId() < 8) {
                    logger.info("Skipping company ID {}: {}", company.getId(), company.getCompanyName());
                    continue;
                }

                String domain = company.getCompanyEmail().split("@")[1];
                String hrEmail = company.getCompanyEmail().startsWith("hr@") ?
                        company.getCompanyEmail() :
                        "hr@" + domain;

                User user = new User();
                user.setEmail(hrEmail);
                user.setName(company.getCompanyName() + " HR");
                user.setPassword(passwordEncoder.encode("12345678"));
                user.setYearsOfExperience(5);
                user.setRoles(Roles.Company);
                user.setCompanyId(company.getId());
                user.setRequestForCompany(true);
                user.setCreatedAt(LocalDateTime.now());
                users.add(user);

                logger.info("Created HR user for company: {} with email: {}",
                        company.getCompanyName(), hrEmail);
            }

            userRepository.saveAll(users);
            logger.info("Successfully created {} HR users", users.size());
        };
    }
}