package com.Bisag.QuizApp.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Bisag.QuizApp.dto.Csuser;
import com.Bisag.QuizApp.entity.Question;
import com.Bisag.QuizApp.entity.Quiz;
import com.Bisag.QuizApp.entity.QuizAttempt;
import com.Bisag.QuizApp.repository.CsuserRepo;
import com.Bisag.QuizApp.service.QuizService;

import jakarta.servlet.http.HttpSession;

@Controller
public class QuizController {
    
    @Autowired
    private QuizService quizService;
    
    @GetMapping("/quizzes")
    @ResponseBody
    public List<Quiz> getActiveQuizzes() {
        return quizService.getAllActiveQuizzes();
    }
    
    @GetMapping("/quiz/{id}")
    public String quizDetail(@PathVariable Long id, Csuser user, Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/?error=Please login to attempt quiz";
        }
        
        // Csuser user = (Csuser) session.getAttribute("loggedInUser");
        // if (user == null) {
        //     return "redirect:/?error=Please login retryt to attempt quiz";
        // }
        
        Quiz quiz = quizService.getQuizById(id);
        if (quiz == null) {
            return "redirect:/?error=Quiz not found";
        }
        
        boolean hasAttempted = quizService.hasUserAttempted(user.getId(), id);
        if (hasAttempted) {
            return "redirect:/?error=You have already attempted this quiz";
        }
        
        List<Question> questions = quizService.getQuestionsByQuizId(id);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        
        return "QuizAttempt";
    }
    
     @Autowired
    private CsuserRepo csuserRepo;

    @PostMapping("/quiz/submit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitQuiz(@RequestBody Map<String, Object> payload, 
                                                       HttpSession session, Principal principal) {
        // Csuser user = (Csuser) session.getAttribute("loggedInUser");
        // if (user == null) {
        //     return ResponseEntity.badRequest().body(Map.of("error", "Please login first"));
        // }
        String email = principal.getName();
    
    // Fetch user from database using email
    Optional<Csuser> userOptional = csuserRepo.findFirstByEmail(email);
    
    if (userOptional.isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
    }

    Csuser user = userOptional.get(); // You need this service method

    if (user == null) {
        return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
    }
         // Fetch user details using email if needed

        Long quizId = Long.valueOf(payload.get("quizId").toString());
        @SuppressWarnings("unchecked")
        Map<String, String> answers = (Map<String, String>) payload.get("answers");
        
        Quiz quiz = quizService.getQuizById(quizId);
        List<Question> questions = quizService.getQuestionsByQuizId(quizId);
        
        int score = 0;
        int correct = 0;
        int wrong = 0;
        
        for (Question q : questions) {
            String userAnswer = answers.get(q.getId().toString());
            if (userAnswer != null && userAnswer.equals(q.getCorrectAnswer())) {
                score += q.getMarks();
                correct++;
            } else {
                wrong++;
            }
        }
        
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUserId(user.getId());
        attempt.setUserEmail(user.getEmail());
        attempt.setQuiz(quiz);
        attempt.setScore(score);
        attempt.setTotalMarks(quiz.getTotalMarks());
        attempt.setCorrectAnswers(correct);
        attempt.setWrongAnswers(wrong);
        attempt.setUserAnswers(answers.toString());
        
        quizService.saveAttempt(attempt);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("totalMarks", quiz.getTotalMarks());
        result.put("correct", correct);
        result.put("wrong", wrong);
        result.put("percentage", (score * 100.0) / quiz.getTotalMarks());
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/admin/quiz/upload")
    public String uploadQuiz(@RequestParam("file") MultipartFile file,
                            @RequestParam("quizName") String quizName,
                            @RequestParam("quizType") String quizType,
                            @RequestParam("quizDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate quizDate,
                            @RequestParam("duration") Integer duration,
                            RedirectAttributes attributes) {
        try {
            quizService.uploadQuizFromExcel(file, quizName, quizType, quizDate, duration);
            attributes.addFlashAttribute("success", "Quiz uploaded successfully");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Failed to upload quiz: " + e.getMessage());
        }
        return "redirect:/dash";
    }
}
