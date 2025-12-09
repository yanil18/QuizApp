package com.Bisag.QuizApp.controllers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Bisag.QuizApp.dto.Csuser;
import com.Bisag.QuizApp.repository.CsuserRepo;


@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
    //         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //           if (auth != null && auth.isAuthenticated()
    //         && !(auth instanceof AnonymousAuthenticationToken)) {
    //     return "redirect:/dash";
    // }
        return "Home";
    }

    @Autowired
    private CsuserRepo csuserRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    String encryption_key = "CAg8AMIICCgKCAgEAv3SWf07W0jarHY59m";

    @PostMapping("/keys")
    public ResponseEntity<Map<String, String>> getKeys() {
        HybridController hc = new HybridController();
        Map<String, String> keys = new HashMap<>();
        keys.put("HarshKey", Base64.getEncoder().encodeToString(encryption_key.getBytes(StandardCharsets.UTF_8)));
        keys.put("AbhiKey", Base64.getEncoder().encodeToString(hc.publicKeyPEM.getBytes(StandardCharsets.UTF_8)));
        return ResponseEntity.ok(keys);
    }

    public static HybridController hybridController = new HybridController();

    public static String Hybriddecrypt(String strToDecrypt) {
        try {
            return hybridController.Hybrid_Data_Decryption(strToDecrypt);
        } catch (Exception e) {

        }
        return null;
    }

    @PostMapping("/signup")
    public String Signup(@ModelAttribute Csuser csuser, RedirectAttributes attributes) {

        if (csuser.getConfirmpassword() == null || !csuser.getConfirmpassword().equals(csuser.getPassword())) {
            attributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/";
        }

        // Check unique email
        if (csuserRepo.findFirstByEmail(csuser.getEmail()).isPresent()) {
            attributes.addFlashAttribute("error", "Email already exists");
            return "redirect:/";
        }

        // Set role as USER (or leave null for regular users)
        csuser.setRole("USER");
        csuser.setPassword(passwordEncoder.encode(csuser.getPassword()));
        csuser.setConfirmpassword(passwordEncoder.encode(csuser.getConfirmpassword()));
        csuserRepo.save(csuser);
        attributes.addFlashAttribute("success", "Registration successful! Please sign in to attempt quizzes.");

        return "redirect:/";
    }


    @GetMapping("/dash")
    public String dashboard() {
        return "Dashboard";
    }
    
    @GetMapping("/login")
    public String adminLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() 
            && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dash";
        }
        return "AdminLogin";
    }
    
}
