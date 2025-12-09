package com.Bisag.QuizApp.controllers;

import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Bisag.QuizApp.dto.Csuser;
import com.Bisag.QuizApp.repository.CsuserRepo;
import com.Bisag.QuizApp.security.CustomAuthenticationFailureHandler;
import com.Bisag.QuizApp.security.CustomAuthenticationSuccessHandler;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    

     @Autowired
    private CsuserRepo csuserRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;


    public boolean authenticateProusers(String email, String password, HttpServletRequest request) {
        Optional<Csuser> csuser = csuserRepo.findFirstByEmail(email);
        if (csuser.isPresent() && passwordEncoder.matches(password, csuser.get().getPassword())) {
            request.getSession().setAttribute("loggedInUser", csuser.get());
            return true;
        }
        return false;

    }

    @GetMapping("/autoauth/{email}/{password}")
    public String myloginredirect(HttpServletRequest request, @PathVariable(value = "email") String email,
            @PathVariable(value = "password") String password, RedirectAttributes attributes, HttpServletResponse response) {
                System.out.println("AutoAuth Attempt for email: " + email);
        try {
             Authentication authentication =  authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
                );
            SecurityContextHolder.getContext().setAuthentication(authentication);
          
            Optional<Csuser> csuser = csuserRepo.findFirstByEmail(email);

            if (authenticateProusers(email, password, request)) {
                request.getSession().setAttribute("loggedInUser", csuser.get());
                 customAuthenticationSuccessHandler.onAuthenticationSuccess(request, response,  authentication);
                return "redirect:/dash";
        }
         else {
                attributes.addFlashAttribute("error", "Invalid Password");
                customAuthenticationFailureHandler.onAuthenticationFailure(request, response, null);
                return "redirect:/";
            }
        }
             catch (Exception e) {
            e.printStackTrace(); // You might want to log this instead
            attributes.addFlashAttribute("error", "An error occurred, please try again later");
            return "redirect:/";
        }
       
    }

    @PostMapping("/login")
    public String userLogin(@RequestParam String email, @RequestParam String password, 
            HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes attributes) {

        System.out.println("User Login Attempt for email: " + email);
        try {
             Authentication authentication =  authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
                );
            SecurityContextHolder.getContext().setAuthentication(authentication);
          
            Optional<Csuser> csuser = csuserRepo.findFirstByEmail(email);

            if (authenticateProusers(email, password, request)) {
                Csuser user = csuser.get();
                request.getSession().setAttribute("loggedInUser", user);
                
                // Check if user is trying to login as admin from user login
                if (user.getRole() != null && user.getRole().equalsIgnoreCase("ADMIN")) {
                    attributes.addFlashAttribute("error", "Admin users should use the Admin Login page");
                    SecurityContextHolder.clearContext();
                    return "redirect:/";
                }
                
                // Don't call success handler here, just redirect
                attributes.addFlashAttribute("success", "Welcome back! You can now attempt quizzes.");
                return "redirect:/";
            } else {
                attributes.addFlashAttribute("error", "Invalid email or password");
                SecurityContextHolder.clearContext();
                return "redirect:/";
            }
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/";
        }
    }
    
    @PostMapping("/admin/login")
    public String adminLogin(@RequestParam String email, @RequestParam String password, 
            HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes attributes) {

        System.out.println("Admin Login Attempt for email: " + email);
        try {
             Authentication authentication =  authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
                );
            SecurityContextHolder.getContext().setAuthentication(authentication);
          
            Optional<Csuser> csuser = csuserRepo.findFirstByEmail(email);

            if (authenticateProusers(email, password, request)) {
                Csuser user = csuser.get();
                request.getSession().setAttribute("loggedInUser", user);
                
                // Check if user has admin role
                if (user.getRole() != null && user.getRole().equalsIgnoreCase("ADMIN")) {
                    // Don't call success handler, just redirect
                    return "redirect:/dash";
                } else {
                    attributes.addFlashAttribute("error", "Access denied. Admin privileges required.");
                    SecurityContextHolder.clearContext();
                    return "redirect:/login";
                }
            } else {
                attributes.addFlashAttribute("error", "Invalid email or password");
                SecurityContextHolder.clearContext();
                return "redirect:/login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Invalid email or password");
            return "redirect:/login";
        }
    }
     
}
