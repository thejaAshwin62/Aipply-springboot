package com.aipply.aipply.config;

import com.aipply.aipply.model.*;
import com.aipply.aipply.projection.JobType;
import com.aipply.aipply.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Configuration
@Profile("dev")
public class TestDataLoader {

    private final Random random = new Random();

    @Bean
    public CommandLineRunner loadTestData(
            CompanyRepository companyRepository,
            JobRepo jobRepository) {
        return args -> {
            List<Company> companies = createCompanies();
            companies = companyRepository.saveAll(companies);

            List<Job> jobs = createJobs(companies);
            jobRepository.saveAll(jobs);
        };
    }

    private List<Company> createCompanies() {
        return Arrays.asList(
                new Company(null, "TechForge Solutions", "hr@techforge.com", "Enterprise software solutions", "https://techforge.com"),
                new Company(null, "DataFlow Systems", "careers@dataflow.io", "Big data and analytics", "https://dataflow.io"),
                new Company(null, "CloudNex", "jobs@cloudnex.com", "Cloud infrastructure provider", "https://cloudnex.com"),
                new Company(null, "AI Dynamics", "recruiting@aidynamics.ai", "AI and ML solutions", "https://aidynamics.ai"),
                new Company(null, "CyberShield", "talent@cybershield.com", "Cybersecurity solutions", "https://cybershield.com"),
                new Company(null, "QuantumTech", "careers@quantumtech.com", "Quantum computing research", "https://quantumtech.com"),
                new Company(null, "DevOps Pro", "hr@devopspro.io", "DevOps consulting", "https://devopspro.io"),
                new Company(null, "BlockChain Ventures", "jobs@bcventures.com", "Blockchain solutions", "https://bcventures.com"),
                new Company(null, "MobileFirst", "careers@mobilefirst.io", "Mobile app development", "https://mobilefirst.io"),
                new Company(null, "WebStack", "hr@webstack.dev", "Web development services", "https://webstack.dev"),
                new Company(null, "IoT Solutions", "jobs@iotsolutions.com", "IoT platform provider", "https://iotsolutions.com"),
                new Company(null, "DataSec Corp", "careers@datasec.com", "Data security", "https://datasec.com"),
                new Company(null, "CloudScale", "hr@cloudscale.io", "Cloud scaling solutions", "https://cloudscale.io"),
                new Company(null, "AI Labs", "talent@ailabs.ai", "AI research", "https://ailabs.ai"),
                new Company(null, "CodeCraft", "jobs@codecraft.dev", "Software development", "https://codecraft.dev"),
                new Company(null, "EdgeCompute", "careers@edgecompute.io", "Edge computing solutions", "https://edgecompute.io"),
                new Company(null, "FinTech Solutions", "hr@fintech.com", "Financial technology", "https://fintech.com"),
                new Company(null, "Health Tech", "jobs@healthtech.io", "Healthcare technology", "https://healthtech.io"),
                new Company(null, "Smart Systems", "careers@smartsys.com", "Smart city solutions", "https://smartsys.com"),
                new Company(null, "DevTools Inc", "hr@devtools.io", "Developer tools", "https://devtools.io"),
                new Company(null, "ML Operations", "jobs@mlops.ai", "ML infrastructure", "https://mlops.ai"),
                new Company(null, "Cloud Security", "careers@cloudsec.com", "Cloud security", "https://cloudsec.com"),
                new Company(null, "Data Analytics", "hr@dataanalytics.io", "Data analytics platform", "https://dataanalytics.io"),
                new Company(null, "Web3 Tech", "jobs@web3tech.com", "Web3 development", "https://web3tech.com"),
                new Company(null, "API Solutions", "careers@apisolutions.io", "API development", "https://apisolutions.io"),
                new Company(null, "Test Automation", "hr@testauto.com", "Testing solutions", "https://testauto.com"),
                new Company(null, "Cloud Native", "jobs@cloudnative.dev", "Cloud-native development", "https://cloudnative.dev"),
                new Company(null, "Security Ops", "careers@secops.io", "Security operations", "https://secops.io"),
                new Company(null, "Data Platforms", "hr@dataplatforms.com", "Data platform solutions", "https://dataplatforms.com"),
                new Company(null, "AI Solutions", "jobs@aisolutions.ai", "AI solutions provider", "https://aisolutions.ai")
        );
    }

    private List<Job> createJobs(List<Company> companies) {
        List<Job> jobs = new ArrayList<>();
        String[] locations = {"New York, NY", "San Francisco, CA", "Austin, TX", "Seattle, WA", "Boston, MA",
                "Chicago, IL", "Remote", "Los Angeles, CA", "Denver, CO", "Atlanta, GA"};

        for (Company company : companies) {
            // Create 2 jobs per company
            for (int i = 0; i < 3; i++) {
                jobs.add(createRandomJob(company, locations));
            }
        }
        return jobs;
    }

    private Job createRandomJob(Company company, String[] locations) {
        JobType[] jobTypes = JobType.values();
        String location = locations[random.nextInt(locations.length)];
        int yearsExp = random.nextInt(8) + 1;
        double baseSalary = 70000.0;
        double salary = baseSalary + (yearsExp * 15000) + random.nextInt(30000);

        return new Job(
                null,
                getRandomJobTitle(),
                getRandomJobDescription(),
                getRandomTechStack(),
                company,
                yearsExp,
                jobTypes[random.nextInt(jobTypes.length)],
                location,
                salary,
                LocalDateTime.now(),
                true,
                70 + random.nextInt(16), // minimum mock score between 70-85
                50 + random.nextInt(31)  // mock interview percentage between 50-80
        );
    }

    private String getRandomJobTitle() {
        String[] titles = {
                "Senior Backend Developer", "Frontend Engineer", "Full Stack Developer", "DevOps Engineer",
                "Cloud Architect", "Data Engineer", "ML Engineer", "Security Engineer", "Mobile Developer",
                "Systems Architect", "QA Engineer", "Site Reliability Engineer", "API Developer",
                "Software Engineer", "Platform Engineer", "Integration Specialist", "Technical Lead"
        };
        return titles[random.nextInt(titles.length)];
    }

    private List<String> getRandomTechStack() {
        String[][] stacks = {
                {"Java", "Spring Boot", "PostgreSQL", "Redis"},
                {"Python", "Django", "MongoDB", "AWS"},
                {"JavaScript", "React", "Node.js", "MySQL"},
                {"Go", "Docker", "Kubernetes", "Prometheus"},
                {"TypeScript", "Angular", "PostgreSQL", "Azure"},
                {"Python", "TensorFlow", "PyTorch", "AWS"},
                {"Java", "Kafka", "Elasticsearch", "Docker"},
                {"React Native", "TypeScript", "GraphQL", "Firebase"},
                {"C#", ".NET Core", "SQL Server", "Azure"},
                {"Ruby", "Rails", "PostgreSQL", "Heroku"}
        };
        return Arrays.asList(stacks[random.nextInt(stacks.length)]);
    }

    private String getRandomJobDescription() {
        String[] descriptions = {
                "Join our team to build scalable backend services and APIs.",
                "Help us create beautiful and responsive user interfaces.",
                "Work on challenging problems in distributed systems.",
                "Build and maintain cloud infrastructure and deployment pipelines.",
                "Design and implement machine learning models for production.",
                "Develop secure and efficient microservices.",
                "Create cross-platform mobile applications.",
                "Optimize and scale our data processing pipelines.",
                "Implement security best practices across our platform.",
                "Build next-generation cloud-native applications."
        };
        return descriptions[random.nextInt(descriptions.length)];
    }
}