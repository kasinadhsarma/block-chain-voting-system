package com.votex.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "candidates")
public class Candidate {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String electionId;
    
    private int voteCount;

    public Candidate() {
        this.voteCount = 0;
    }

    public Candidate(String name, String description, String electionId) {
        this.name = name;
        this.description = description;
        this.electionId = electionId;
        this.voteCount = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getElectionId() {
        return electionId;
    }

    public void setElectionId(String electionId) {
        this.electionId = electionId;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void incrementVoteCount() {
        this.voteCount++;
    }
}