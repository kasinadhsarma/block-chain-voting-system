package com.votex.repository;

import com.votex.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, String> {
    List<Candidate> findByElectionId(String electionId);
}