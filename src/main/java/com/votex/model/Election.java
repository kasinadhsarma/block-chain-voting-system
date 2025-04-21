package com.votex.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "elections")
public class Election {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> candidateIds;

    public Election() {
        this.candidateIds = new ArrayList<>();
        this.isActive = false;
    }
    
    public Election(String title, String description) {
        this.title = title;
        this.description = description;
        this.candidateIds = new ArrayList<>();
        this.isActive = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<String> getCandidateIds() {
        return candidateIds;
    }

    public void setCandidateIds(List<String> candidateIds) {
        this.candidateIds = candidateIds;
    }
    
    public void addCandidateId(String candidateId) {
        if (this.candidateIds == null) {
            this.candidateIds = new ArrayList<>();
        }
        this.candidateIds.add(candidateId);
    }
}