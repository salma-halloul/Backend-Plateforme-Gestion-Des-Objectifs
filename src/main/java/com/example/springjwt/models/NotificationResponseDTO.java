package com.example.springjwt.models;

import java.time.LocalDateTime;

//classe de showbyid des notification pour afficher le contenu de l'objectif
public class NotificationResponseDTO {
    private Long id;
    private String message;
    private LocalDateTime creationDate;
    private String ownerUsername;  // de la notification
    private Objective relatedObjective;  // détails de l'objectif lié

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Objective getRelatedObjective() {
        return relatedObjective;
    }

    public void setRelatedObjective(Objective relatedObjective) {
        this.relatedObjective = relatedObjective;
    }
}
