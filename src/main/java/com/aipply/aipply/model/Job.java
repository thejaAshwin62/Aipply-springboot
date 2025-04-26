package com.aipply.aipply.model;

import com.aipply.aipply.projection.JobType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Entity
@Table(name = "job")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    private static final Logger logger = LoggerFactory.getLogger(Job.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "tech_stack", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> techStack = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Column(columnDefinition = "TEXT")
    private String location;

    private Double salary;

    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    @Column(name = "mock_interview_required")
    private Boolean mockInterviewRequired = true;

    @Column(name = "minimum_mock_score")
    private Integer minimumMockScore;

    @Column(name = "mock_interview_percentage")
    private Integer mockInterviewPercentage;
}

@Converter
class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final Logger logger = LoggerFactory.getLogger(StringListConverter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Error converting list to JSON: {}", e.getMessage());
            return "[]";
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            // First try to parse as JSON
            return objectMapper.readValue(dbData, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            logger.warn("Failed to parse as JSON, trying as comma-separated string: {}", e.getMessage());
            try {
                // If JSON parsing fails, try to parse as comma-separated string
                if (dbData.startsWith("[") && dbData.endsWith("]")) {
                    // Remove brackets and split
                    String content = dbData.substring(1, dbData.length() - 1);
                    return Arrays.stream(content.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                } else {
                    // Split by comma directly
                    return Arrays.stream(dbData.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                }
            } catch (Exception ex) {
                logger.error("Error parsing comma-separated string: {}", ex.getMessage());
                return new ArrayList<>();
            }
        }
    }
}
