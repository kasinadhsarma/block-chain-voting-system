package com.votex.repository;

import com.votex.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoterRepository extends JpaRepository<Voter, String> {
    List<Voter> findByVerified(boolean verified);
    Optional<Voter> findByEmail(String email);
}