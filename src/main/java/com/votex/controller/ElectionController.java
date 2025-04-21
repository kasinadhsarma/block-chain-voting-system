package com.votex.controller;

import com.votex.model.Election;
import com.votex.model.Candidate;
import com.votex.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/elections")
public class ElectionController {

    private final ElectionService electionService;

    @Autowired
    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @PostMapping
    public ResponseEntity<Election> createElection(@RequestBody Election election) {
        return new ResponseEntity<>(electionService.createElection(election), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Election>> getAllElections() {
        return ResponseEntity.ok(electionService.getAllElections());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Election> getElectionById(@PathVariable String id) {
        Optional<Election> election = electionService.getElectionById(id);
        return election.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Election> startElection(@PathVariable String id) {
        Election election = electionService.startElection(id);
        if (election != null) {
            return ResponseEntity.ok(election);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<Election> endElection(@PathVariable String id) {
        Election election = electionService.endElection(id);
        if (election != null) {
            return ResponseEntity.ok(election);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/candidates")
    public ResponseEntity<Candidate> addCandidate(@PathVariable String id, @RequestBody Candidate candidate) {
        candidate.setElectionId(id);
        Candidate savedCandidate = electionService.addCandidate(candidate);
        if (savedCandidate != null) {
            return new ResponseEntity<>(savedCandidate, HttpStatus.CREATED);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/candidates")
    public ResponseEntity<List<Candidate>> getCandidatesByElectionId(@PathVariable String id) {
        return ResponseEntity.ok(electionService.getCandidatesByElectionId(id));
    }
}