package com.votex.service;

import com.votex.model.Election;
import com.votex.model.Candidate;
import com.votex.repository.ElectionRepository;
import com.votex.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ElectionService {
    
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    
    @Autowired
    public ElectionService(ElectionRepository electionRepository, CandidateRepository candidateRepository) {
        this.electionRepository = electionRepository;
        this.candidateRepository = candidateRepository;
    }
    
    public Election createElection(Election election) {
        election.setActive(false);
        return electionRepository.save(election);
    }
    
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }
    
    public Optional<Election> getElectionById(String id) {
        return electionRepository.findById(id);
    }
    
    public Election startElection(String id) {
        Optional<Election> optElection = electionRepository.findById(id);
        if (optElection.isPresent()) {
            Election election = optElection.get();
            election.setActive(true);
            election.setStartDate(LocalDateTime.now());
            return electionRepository.save(election);
        }
        return null;
    }
    
    public Election endElection(String id) {
        Optional<Election> optElection = electionRepository.findById(id);
        if (optElection.isPresent()) {
            Election election = optElection.get();
            election.setActive(false);
            election.setEndDate(LocalDateTime.now());
            return electionRepository.save(election);
        }
        return null;
    }
    
    public Candidate addCandidate(Candidate candidate) {
        Optional<Election> election = electionRepository.findById(candidate.getElectionId());
        if (election.isPresent()) {
            Candidate savedCandidate = candidateRepository.save(candidate);
            Election electionEntity = election.get();
            electionEntity.addCandidateId(savedCandidate.getId());
            electionRepository.save(electionEntity);
            return savedCandidate;
        }
        return null;
    }
    
    public List<Candidate> getCandidatesByElectionId(String electionId) {
        return candidateRepository.findByElectionId(electionId);
    }
}