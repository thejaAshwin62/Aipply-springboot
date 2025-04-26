import express from 'express';
import cors from 'cors';
import { GoogleGenerativeAI } from '@google/generative-ai';
import dotenv from 'dotenv';

dotenv.config();

const app = express();
const port = 3001;

// Middleware
app.use(cors());
app.use(express.json());

// Initialize Gemini AI
const genAI = new GoogleGenerativeAI(process.env.GOOGLE_API_KEY);
const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash-lite" });

// Function to clean JSON string
function cleanJsonString(str) {
    // Remove any text before the first [ and after the last ]
    const match = str.match(/\[[\s\S]*\]/);
    if (!match) return null;
    
    let cleaned = match[0];
    
    // Remove markdown code blocks
    cleaned = cleaned.replace(/```json|```/g, '');
    
    // Remove control characters
    cleaned = cleaned.replace(/[\u0000-\u001F\u007F-\u009F]/g, '');
    
    // Remove any trailing commas
    cleaned = cleaned.replace(/,(\s*[}\]])/g, '$1');
    
    // Ensure proper JSON structure
    cleaned = cleaned.replace(/([{,])\s*([a-zA-Z0-9_]+)\s*:/g, '$1"$2":');
    
    return cleaned.trim();
}

// Function to truncate text to max length
function truncateText(text, maxLength = 255) {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength - 3) + '...';
}

// Endpoint to generate mock questions
app.post('/generate-questions', async (req, res) => {
    try {
        const { techStack, jobDescription, yearsOfExperience } = req.body;

        // Validate input
        if (!techStack || !jobDescription || !yearsOfExperience) {
            return res.status(400).json({ 
                success: false,
                error: 'Missing required fields' 
            });
        }

        const prompt = `Generate exactly 5 technical interview questions and answers in the following exact JSON format:

[
  {
    "question": "Question text here",
    "answer": "Detailed answer here"
  },
  {
    "question": "Question text here",
    "answer": "Detailed answer here"
  }
]

Job Position: ${techStack}
Job Description: ${jobDescription}
Years of Experience: ${yearsOfExperience}

IMPORTANT:
1. Return ONLY the JSON array, no additional text
2. Each question should be specific to the job position and experience level
3. Answers should be concise and to the point (max 250 characters)
4. Format the JSON exactly as shown above
5. Do not include any markdown formatting or code blocks`;

        const result = await model.generateContent(prompt);
        const response = await result.response;
        const text = response.text();

        // Clean the response
        const cleanedResponse = cleanJsonString(text);
        if (!cleanedResponse) {
            return res.status(500).json({ 
                success: false,
                error: 'Could not extract valid JSON from response' 
            });
        }

        // Parse the cleaned response
        const parsedResponse = JSON.parse(cleanedResponse);
        
        // Validate the parsed response
        if (!Array.isArray(parsedResponse)) {
            return res.status(500).json({ 
                success: false,
                error: 'Invalid response format: expected an array' 
            });
        }

        // Validate each question and truncate answers
        const validQuestions = parsedResponse
            .filter(q => 
                q && typeof q === 'object' && 
                typeof q.question === 'string' && 
                typeof q.answer === 'string' &&
                q.question.trim() !== '' &&
                q.answer.trim() !== ''
            )
            .map(q => ({
                question: q.question.trim(),
                answer: truncateText(q.answer.trim())
            }));

        if (validQuestions.length === 0) {
            return res.status(500).json({ 
                success: false,
                error: 'No valid questions generated' 
            });
        }

        res.json({
            success: true,
            data: validQuestions
        });
    } catch (error) {
        console.error('Error generating questions:', error);
        res.status(500).json({ 
            success: false,
            error: 'Failed to generate questions',
            details: error.message
        });
    }
});

// Endpoint to generate feedback for user answers
app.post('/generate-feedback', async (req, res) => {
    try {
        const { question, correctAnswer, userAnswer } = req.body;

        // Validate input
        if (!question || !correctAnswer || !userAnswer) {
            return res.status(400).json({ 
                success: false,
                error: 'Missing required fields' 
            });
        }

        const prompt = `Evaluate the following interview answer and provide feedback:

Question: ${question}
Correct Answer: ${correctAnswer}
User's Answer: ${userAnswer}

Please provide:
1. Detailed feedback on the answer's accuracy, completeness, and technical depth
2. A rating from 1-5 (where 5 is perfect)
3. Specific areas for improvement
4. make the feedback concise and to the point short and simple

Return the response in the following JSON format:
{
    "feedback": "Detailed feedback here",
    "rating": 8
}`;

        const result = await model.generateContent(prompt);
        const response = await result.response;
        const text = response.text();

        // Clean and parse the response
        const cleanedResponse = text.replace(/```json|```/g, '').trim();
        const parsedResponse = JSON.parse(cleanedResponse);

        res.json({
            success: true,
            data: parsedResponse
        });
    } catch (error) {
        console.error('Error generating feedback:', error);
        res.status(500).json({ 
            success: false,
            error: 'Failed to generate feedback',
            details: error.message
        });
    }
});

app.listen(port, () => {
    console.log(`Node.js server running on port ${port}`);
});
