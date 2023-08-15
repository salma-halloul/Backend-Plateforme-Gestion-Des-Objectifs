package com.example.springjwt.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "objectives")
public class Objective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private EStatus status;


    @Column
    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column
    private double percentage;

    @ManyToMany
    @JoinTable(name = "shared_objectives",
            joinColumns = @JoinColumn(name = "objective_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> sharedWith;


    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
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

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public List<User> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(List<User> sharedWith) {
        this.sharedWith = sharedWith;
    }
}

