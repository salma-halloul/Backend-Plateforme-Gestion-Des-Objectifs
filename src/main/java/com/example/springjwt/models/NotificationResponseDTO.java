package com.example.springjwt.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

//classe de showbyid des notification pour afficher le contenu de l'objectif et de suggestion
public class NotificationResponseDTO {
    private Long id;
    private String message;
    private LocalDateTime creationDate;
    private String ownerUsername;  // de la notification

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Objective relatedObjective;  // détails de l'objectif lié

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SuggestionObjective relatedSuggestion;

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

    public SuggestionObjective getRelatedSuggestion() {
        return relatedSuggestion;
    }

    public void setRelatedSuggestion(SuggestionObjective relatedSuggestion) {
        this.relatedSuggestion = relatedSuggestion;
    }
}
