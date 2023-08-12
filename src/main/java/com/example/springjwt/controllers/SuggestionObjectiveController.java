package com.example.springjwt.controllers;

import com.example.springjwt.models.SuggestionObjective;
import com.example.springjwt.security.services.UserDetailsImpl;
import com.example.springjwt.service.SuggestionObjectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
public class SuggestionObjectiveController {

    private final SuggestionObjectiveService suggestionObjectiveService;

    @Autowired
    public SuggestionObjectiveController(SuggestionObjectiveService suggestionObjectiveService) {
        this.suggestionObjectiveService = suggestionObjectiveService;
    }

    @GetMapping
    public List<SuggestionObjective> getAllSuggestions() {
        return suggestionObjectiveService.getAllSuggestions();
    }


    @GetMapping("/{id}")
    public ResponseEntity<SuggestionObjective> getSuggestionById(@PathVariable Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SuggestionObjective suggestion = suggestionObjectiveService.getSuggestionById(id, userDetails);
        return ResponseEntity.ok(suggestion);
    }

    @PostMapping
    public ResponseEntity<SuggestionObjective> createSuggestion(@RequestBody SuggestionObjective suggestion) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(suggestionObjectiveService.saveSuggestion(suggestion, userDetails));
    }

    @PostMapping("/{id}/approve")
    public SuggestionObjective approveSuggestion(@PathVariable Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return suggestionObjectiveService.approveOrRejectSuggestion(id, true, userDetails);
    }

    @PostMapping("/{id}/reject")
    public SuggestionObjective rejectSuggestion(@PathVariable Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return suggestionObjectiveService.approveOrRejectSuggestion(id, false, userDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuggestion(@PathVariable Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        suggestionObjectiveService.deleteSuggestion(id, userDetails);
        return ResponseEntity.noContent().build();
    }

}
