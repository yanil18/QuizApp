package com.Bisag.QuizApp.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Bisag.QuizApp.entity.Question;
import com.Bisag.QuizApp.entity.Quiz;
import com.Bisag.QuizApp.entity.QuizAttempt;
import com.Bisag.QuizApp.repository.QuestionRepo;
import com.Bisag.QuizApp.repository.QuizAttemptRepo;
import com.Bisag.QuizApp.repository.QuizRepo;

@Service
public class QuizService {
    
    @Autowired
    private QuizRepo quizRepo;
    
    @Autowired
    private QuestionRepo questionRepo;
    
    @Autowired
    private QuizAttemptRepo quizAttemptRepo;
    
    public List<Quiz> getAllActiveQuizzes() {
        return quizRepo.findByIsActiveTrue();
    }
    
    public Quiz getQuizById(Long id) {
        return quizRepo.findById(id).orElse(null);
    }
    
    public List<Question> getQuestionsByQuizId(Long quizId) {
        return questionRepo.findByQuizId(quizId);
    }
    
    public boolean hasUserAttempted(Long userId, Long quizId) {
        return quizAttemptRepo.findByUserIdAndQuizId(userId, quizId).isPresent();
    }
    
    public QuizAttempt saveAttempt(QuizAttempt attempt) {
        return quizAttemptRepo.save(attempt);
    }
    
    public List<QuizAttempt> getUserAttempts(Long userId) {
        return quizAttemptRepo.findByUserId(userId);
    }
    
    public Quiz uploadQuizFromExcel(MultipartFile file, String quizName, String quizType, 
                                    LocalDate quizDate, Integer durationMinutes) throws Exception {
        Quiz quiz = new Quiz();
        quiz.setQuizName(quizName);
        quiz.setQuizType(quizType);
        quiz.setQuizDate(quizDate);
        quiz.setDurationMinutes(durationMinutes);
        quiz.setIsActive(true);
        
        List<Question> questions = new ArrayList<>();
        int totalMarks = 0;
        
        try (InputStream is = file.getInputStream(); 
             Workbook workbook = new XSSFWorkbook(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Question question = new Question();
                question.setQuiz(quiz);
                question.setQuestionText(getCellValue(row.getCell(0)));
                question.setOptionA(getCellValue(row.getCell(1)));
                question.setOptionB(getCellValue(row.getCell(2)));
                question.setOptionC(getCellValue(row.getCell(3)));
                question.setOptionD(getCellValue(row.getCell(4)));
                question.setCorrectAnswer(getCellValue(row.getCell(5)));
                question.setMarks(Integer.parseInt(getCellValue(row.getCell(6))));
                
                questions.add(question);
                totalMarks += question.getMarks();
            }
        }
        
        quiz.setTotalQuestions(questions.size());
        quiz.setTotalMarks(totalMarks);
        quiz.setQuestions(questions);
        
        return quizRepo.save(quiz);
    }
    
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return "";
        }
    }
}
