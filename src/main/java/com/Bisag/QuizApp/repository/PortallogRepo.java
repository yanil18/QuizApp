package com.Bisag.QuizApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Bisag.QuizApp.entity.Portallog;

@Repository
public interface PortallogRepo extends JpaRepository<Portallog, Long> {
    
}
