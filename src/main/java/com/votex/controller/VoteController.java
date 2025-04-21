package com.votex.controller;

import com.votex.model.Vote;
import com.votex.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VotingService votingService;

    @Autowired
    public VoteController(VotingService votingService) {
        this.votingService = votingService;
    }

    @PostMapping
    public ResponseEntity<?> castVote(@RequestBody Vote vote) {
        try {
            Vote castedVote = votingService.castVote(vote);
            return new ResponseEntity<>(castedVote, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Vote>> getAllVotes() {
        return ResponseEntity.ok(votingService.getAllVotes());
    }

    @GetMapping("/blockchain/verify")
    public ResponseEntity<Map<String, Boolean>> verifyBlockchain() {
        boolean isValid = votingService.isBlockchainValid();
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    @GetMapping("/count/{candidateId}")
    public ResponseEntity<Map<String, Integer>> getVoteCountForCandidate(@PathVariable String candidateId) {
        int count = votingService.getVoteCountForCandidate(candidateId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}