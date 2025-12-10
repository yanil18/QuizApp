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
            return "redirect:/QuizApp?error=Please login to attempt quiz";
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
        result.put("quizId", quizId);
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
    
    @GetMapping("/quiz/{id}/solution")
    public String quizSolution(@PathVariable Long id, Model model, Principal principal) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/?error=Please login to view solution";
        }
        
        String email = principal.getName();
        Optional<Csuser> userOptional = csuserRepo.findFirstByEmail(email);
        
        if (userOptional.isEmpty()) {
            return "redirect:/?error=User not found";
        }
        
        Csuser user = userOptional.get();
        Quiz quiz = quizService.getQuizById(id);
        
        if (quiz == null) {
            return "redirect:/?error=Quiz not found";
        }
        
        // Check if user has attempted this quiz
        boolean hasAttempted = quizService.hasUserAttempted(user.getId(), id);
        if (!hasAttempted) {
            return "redirect:/quiz/" + id + "?error=Please attempt the quiz first to view solution";
        }
        
        // Get user's latest attempt for this quiz
        QuizAttempt attempt = quizService.getUserLatestAttempt(user.getId(), id);
        List<Question> questions = quizService.getQuestionsByQuizId(id);
        
        // Parse user answers from attempt
        Map<Long, String> userAnswers = new HashMap<>();
        if (attempt != null && attempt.getUserAnswers() != null) {
            // Parse the stored answers (assuming they're stored as JSON-like string)
            String answersStr = attempt.getUserAnswers();
            // Handle both JSON format and simple key=value format
            if (answersStr.startsWith("{") && answersStr.endsWith("}")) {
                // JSON-like format: {1=A, 2=B, 3=C}
                answersStr = answersStr.replace("{", "").replace("}", "");
                String[] pairs = answersStr.split(", ");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        try {
                            Long questionId = Long.valueOf(keyValue[0].trim());
                            String answer = keyValue[1].trim();
                            userAnswers.put(questionId, answer);
                        } catch (NumberFormatException e) {
                            // Skip invalid entries
                        }
                    }
                }
            } else {
                // Try to parse as simple format
                String[] pairs = answersStr.split(",");
                for (String pair : pairs) {
                    String[] keyValue = pair.split(":");
                    if (keyValue.length == 2) {
                        try {
                            Long questionId = Long.valueOf(keyValue[0].trim().replace("\"", ""));
                            String answer = keyValue[1].trim().replace("\"", "");
                            userAnswers.put(questionId, answer);
                        } catch (NumberFormatException e) {
                            // Skip invalid entries
                        }
                    }
                }
            }
        }
        
        // Create result summary
        Map<String, Object> result = new HashMap<>();
        result.put("score", attempt.getScore());
        result.put("totalMarks", attempt.getTotalMarks());
        result.put("correct", attempt.getCorrectAnswers());
        result.put("wrong", attempt.getWrongAnswers());
        result.put("percentage", (attempt.getScore() * 100.0) / attempt.getTotalMarks());
        
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        model.addAttribute("userAnswers", userAnswers);
        model.addAttribute("result", result);
        
        return "QuizAttemptSolution";
    }
}
