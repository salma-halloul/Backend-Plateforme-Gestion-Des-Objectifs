package com.example.springjwt.models;

import java.time.LocalDateTime;

public class ObjectiveWithOwnerDTO {
    private Long id;
    private String description;
    private EStatus status;
    private LocalDateTime deadline;
    private String ownerUsername;

    public ObjectiveWithOwnerDTO(Objective objective, String ownerUsername) {
        this.id = objective.getId();
        this.description = objective.getDescription();
        this.status = objective.getStatus();
        this.deadline = objective.getDeadline();
        this.ownerUsername = ownerUsername;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
}
