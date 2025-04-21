package com.votex.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.votex.model.Vote;
import com.votex.service.VotingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class VoteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VotingService votingService;

    @InjectMocks
    private VoteController voteController;

    private ObjectMapper objectMapper;
    private Vote testVote;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(voteController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDateTime serialization

        // Create a test vote
        testVote = new Vote();
        testVote.setId("vote1");
        testVote.setVoterId("voter1");
        testVote.setCandidateId("candidate1");
        testVote.setElectionId("election1");
        testVote.setTimestamp(LocalDateTime.now());
        testVote.setSignature("signature123");
    }

    @Test
    public void testCastVote_Success() throws Exception {
        // Mock service
        when(votingService.castVote(any(Vote.class))).thenReturn(testVote);

        // Perform request and validation
        mockMvc.perform(post("/api/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testVote)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("vote1"))
                .andExpect(jsonPath("$.voterId").value("voter1"))
                .andExpect(jsonPath("$.candidateId").value("candidate1"))
                .andExpect(jsonPath("$.electionId").value("election1"));
    }

    @Test
    public void testCastVote_Error() throws Exception {
        // Mock service throwing exception
        when(votingService.castVote(any(Vote.class))).thenThrow(new IllegalStateException("Voter already voted"));

        // Perform request and validation
        mockMvc.perform(post("/api/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testVote)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Voter already voted"));
    }

    @Test
    public void testGetAllVotes() throws Exception {
        // Mock service
        Vote vote1 = new Vote("voter1", "candidate1", "election1");
        vote1.setId("vote1");
        Vote vote2 = new Vote("voter2", "candidate2", "election1");
        vote2.setId("vote2");

        when(votingService.getAllVotes()).thenReturn(Arrays.asList(vote1, vote2));

        // Perform request and validation
        mockMvc.perform(get("/api/votes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("vote1"))
                .andExpect(jsonPath("$[0].voterId").value("voter1"))
                .andExpect(jsonPath("$[1].id").value("vote2"))
                .andExpect(jsonPath("$[1].voterId").value("voter2"));
    }

    @Test
    public void testVerifyBlockchain() throws Exception {
        // Mock service
        when(votingService.isBlockchainValid()).thenReturn(true);

        // Perform request and validation
        mockMvc.perform(get("/api/votes/blockchain/verify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    public void testGetVoteCountForCandidate() throws Exception {
        // Mock service
        String candidateId = "candidate1";
        when(votingService.getVoteCountForCandidate(candidateId)).thenReturn(42);

        // Perform request and validation
        mockMvc.perform(get("/api/votes/count/{candidateId}", candidateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(42));
    }
}