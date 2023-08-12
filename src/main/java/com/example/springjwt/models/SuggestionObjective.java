package com.example.springjwt.models;

import jakarta.persistence.*;


@Entity
@Table(name = "suggestion_objectives")
public class SuggestionObjective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private EStatusSug status;

    @ManyToOne
    @JoinColumn(name = "suggested_by_id")
    private User suggestedBy;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;


    public SuggestionObjective() {}

    public SuggestionObjective(String description, EStatusSug status, User suggestedBy, User manager) {
        this.description = description;
        this.status = status;
        this.suggestedBy = suggestedBy;
        this.manager = manager;
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

    public EStatusSug getStatus() {
        return status;
    }

    public void setStatus(EStatusSug status) {
        this.status = status;
    }

    public User getSuggestedBy() {
        return suggestedBy;
    }

    public void setSuggestedBy(User suggestedBy) {
        this.suggestedBy = suggestedBy;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }
}

