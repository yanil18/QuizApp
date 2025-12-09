package com.Bisag.QuizApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Bisag.QuizApp.dto.Csuser;

import jakarta.transaction.Transactional;

    @Repository
    @Transactional
    public interface CsuserRepo extends JpaRepository<Csuser, Long> { 

        Optional<Csuser> findFirstByEmail(String email);
        
    }
