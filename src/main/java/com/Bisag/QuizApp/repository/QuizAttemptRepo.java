package com.Bisag.QuizApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Bisag.QuizApp.entity.QuizAttempt;

@Repository
public interface QuizAttemptRepo extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserId(Long userId);
    Optional<QuizAttempt> findByUserIdAndQuizId(Long userId, Long quizId);
}
