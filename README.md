# Aipply - AI Job Application Platform

AI-powered job platform with mock interviews, job posting, and candidate management.

## 🏗️ Architecture

- **Backend**: Spring Boot (Java 21)
- **Frontend**: React + Vite
- **AI Service**: Node.js + Google Gemini AI

## 📋 Prerequisites

- Java 21+, Node.js 18+, Maven 3.6+, Git

## 🚀 Setup

### 1. Clone & Environment

```bash
git clone https://github.com/thejaAshwin62/Aipply-springboot.git
cd Aipply-springboot
```

Create `NodeMockQuestionService/.env`:

```bash
GOOGLE_API_KEY=your_google_gemini_api_key_here
```

Get API key from [Google AI Studio](https://makersuite.google.com/app/apikey)

### 2. Configure Database

Update `src/main/resources/application.properties` with these credentials:

```properties
spring.datasource.url=jdbc:postgresql://ep-white-wave-a412g37f-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=neondb_owner
spring.datasource.password=npg_SY7spy9oxqEJ
```

### 3. Important: Update AI Service URL

In `src/main/java/com/aipply/aipply/service/MockQuestionService.java`, replace lines 70-74:

```java
ResponseEntity<String> response = restTemplate.postForEntity(
        "https://mockai-service.onrender.com/generate-questions",
        request,
        String.class
);
```

with:

```java
ResponseEntity<String> response = restTemplate.postForEntity(
        "http://localhost:3001/generate-questions",
        request,
        String.class
);
```

### 4. Run Services

**Terminal 1 - Backend:**

```bash
mvn spring-boot:run
```

**Terminal 2 - Frontend:**

```bash
cd client
npm install && npm run dev
```

**Terminal 3 - AI Service:**

```bash
cd NodeMockQuestionService
npm install && node index.js
```

## 📱 URLs

- Frontend: http://localhost:5173
- Backend: http://localhost:8080
- AI Service: http://localhost:3001

## ☁️ Cloud Deployment

When deploying to the cloud, update CORS configuration in `src/main/java/com/aipply/aipply/config/SecurityConfig.java`:

Add your deployed domain URLs to the `corsConfigurationSource()` method:

```java
configuration.setAllowedOriginPatterns(Arrays.asList(
        "https://your-backend-domain.com",        // Add your backend domain
        "https://your-frontend-domain.com",       // Add your frontend domain
        "https://aipply-springboot-production.up.railway.app",
        "https://aipply-silk.vercel.app",
        "http://localhost:*"
));
```

## 🔧 Troubleshooting

1. **AI Service Error**: Ensure you updated the URL in MockQuestionService.java (lines 70-74) and have valid Google API key
2. **Database Error**: Ensure you updated application.properties with the provided Neon credentials
3. **Port conflicts**: Check ports 8080, 5173, 3001 are available
4. **CORS Issues**: For cloud deployment, ensure your domains are added to SecurityConfig.java
