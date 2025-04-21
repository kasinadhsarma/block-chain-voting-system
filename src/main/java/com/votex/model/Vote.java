package com.votex.model;

import java.time.LocalDateTime;

public class Vote {
    private String id;
    private String voterId;
    private String candidateId;
    private String electionId;
    private LocalDateTime timestamp;
    private String signature;

    public Vote() {
        this.timestamp = LocalDateTime.now();
    }

    public Vote(String voterId, String candidateId, String electionId) {
        this.voterId = voterId;
        this.candidateId = candidateId;
        this.electionId = electionId;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getElectionId() {
        return electionId;
    }

    public void setElectionId(String electionId) {
        this.electionId = electionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "voterId='" + voterId + '\'' +
                ", candidateId='" + candidateId + '\'' +
                ", electionId='" + electionId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}