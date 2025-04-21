package com.votex.service;

import com.votex.blockchain.Blockchain;
import com.votex.crypto.RSAUtil;
import com.votex.model.Candidate;
import com.votex.model.Election;
import com.votex.model.Vote;
import com.votex.model.Voter;
import com.votex.repository.CandidateRepository;
import com.votex.repository.ElectionRepository;
import com.votex.repository.VoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VotingServiceTest {

    @Mock
    private VoterRepository voterRepository;

    @Mock
    private ElectionRepository electionRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private Blockchain blockchain;

    @InjectMocks
    private VotingService votingService;

    private Voter voter;
    private Election election;
    private Candidate candidate;
    private Vote vote;
    private KeyPair keyPair;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Create test data
        keyPair = RSAUtil.generateKeyPair();
        
        voter = new Voter("voter1", "Test Voter", "voter@example.com");
        voter.setVerified(true);
        voter.setHasVoted(false);
        voter.setPublicKey(RSAUtil.getPublicKeyString(keyPair.getPublic()));

        election = new Election("Test Election", "Description of test election");
        election.setId("election1");
        election.setActive(true);
        election.setStartDate(LocalDateTime.now().minusDays(1));
        election.setEndDate(LocalDateTime.now().plusDays(1));

        candidate = new Candidate("Test Candidate", "Description of test candidate", "election1");
        candidate.setId("candidate1");
        candidate.setVoteCount(0);

        vote = new Vote("voter1", "candidate1", "election1");
        vote.setTimestamp(LocalDateTime.now());
        
        // Sign the vote
        vote.setSignature(RSAUtil.sign(vote.toString(), keyPair.getPrivate()));

        // Setup mocks
        when(voterRepository.findById("voter1")).thenReturn(Optional.of(voter));
        when(candidateRepository.findById("candidate1")).thenReturn(Optional.of(candidate));
        when(electionRepository.findById("election1")).thenReturn(Optional.of(election));
    }

    @Test
    public void testCastVote_SuccessfulVote() throws Exception {
        // Act
        Vote result = votingService.castVote(vote);

        // Assert
        assertNotNull(result);
        verify(voterRepository).findById("voter1");
        verify(candidateRepository).findById("candidate1");
        verify(voterRepository).save(any(Voter.class));
        verify(candidateRepository).save(any(Candidate.class));
        verify(blockchain).addBlock(anyList());

        // Verify voter is marked as having voted
        assertTrue(voter.isHasVoted());
        
        // Verify candidate vote count is increased
        assertEquals(1, candidate.getVoteCount());
    }

    @Test
    public void testCastVote_VoterAlreadyVoted() {
        // Arrange
        voter.setHasVoted(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            votingService.castVote(vote);
        });

        assertEquals("Voter is not eligible to vote", exception.getMessage());
        verify(voterRepository).findById("voter1");
        verify(voterRepository, never()).save(any(Voter.class));
        verify(candidateRepository, never()).save(any(Candidate.class));
        verify(blockchain, never()).addBlock(anyList());
    }

    @Test
    public void testCastVote_VoterNotVerified() {
        // Arrange
        voter.setVerified(false);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            votingService.castVote(vote);
        });

        assertEquals("Voter is not verified", exception.getMessage());
        verify(voterRepository).findById("voter1");
        verify(voterRepository, never()).save(any(Voter.class));
        verify(candidateRepository, never()).save(any(Candidate.class));
        verify(blockchain, never()).addBlock(anyList());
    }

    @Test
    public void testCastVote_InvalidSignature() throws Exception {
        // Arrange - Create another keypair to sign the vote incorrectly
        KeyPair anotherKeyPair = RSAUtil.generateKeyPair();
        vote.setSignature(RSAUtil.sign(vote.toString(), anotherKeyPair.getPrivate()));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            votingService.castVote(vote);
        });

        assertTrue(exception.getMessage().contains("Invalid vote signature"));
        verify(voterRepository).findById("voter1");
        verify(voterRepository, never()).save(any(Voter.class));
        verify(candidateRepository, never()).save(any(Candidate.class));
        verify(blockchain, never()).addBlock(anyList());
    }

    @Test
    public void testGetVotesFromBlockchain() {
        // Arrange - Create mock blocks with transactions
        com.votex.blockchain.Block genesisBlock = mock(com.votex.blockchain.Block.class);
        when(genesisBlock.getTransactions()).thenReturn(new ArrayList<>()); // Empty transactions for genesis block
        
        com.votex.blockchain.Block block1 = mock(com.votex.blockchain.Block.class);
        when(block1.getTransactions()).thenReturn(Arrays.asList("vote1", "vote2"));
        
        com.votex.blockchain.Block block2 = mock(com.votex.blockchain.Block.class);
        when(block2.getTransactions()).thenReturn(Arrays.asList("vote3"));
        
        // Set up the blockchain mock
        when(blockchain.getChain()).thenReturn(Arrays.asList(genesisBlock, block1, block2));
        
        // Expected votes - all transactions from non-genesis blocks
        List<String> expectedVotes = Arrays.asList("vote1", "vote2", "vote3");
        
        // Act
        List<String> result = votingService.getVotesFromBlockchain();
        
        // Assert
        assertEquals(expectedVotes.size(), result.size());
        assertTrue(result.containsAll(expectedVotes));
    }

    @Test
    public void testIsBlockchainValid() {
        // Arrange
        when(blockchain.isChainValid()).thenReturn(true);

        // Act
        boolean result = votingService.isBlockchainValid();

        // Assert
        assertTrue(result);
        verify(blockchain).isChainValid();
    }
}