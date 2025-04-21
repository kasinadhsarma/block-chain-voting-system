package com.votex.repository;

import com.votex.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, String> {
    List<Election> findByIsActive(boolean isActive);
    List<Election> findByEndDateAfter(LocalDateTime date);
}