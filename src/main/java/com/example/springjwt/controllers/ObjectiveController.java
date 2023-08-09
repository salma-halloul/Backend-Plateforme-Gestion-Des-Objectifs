package com.example.springjwt.controllers;

import com.example.springjwt.models.ObjectiveUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.springjwt.models.Objective;
import com.example.springjwt.service.ObjectiveService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.springjwt.security.services.UserDetailsImpl;


import java.util.List;

@RestController
@RequestMapping("/api/objectives")
public class ObjectiveController {

    private final ObjectiveService objectiveService;

    @Autowired
    public ObjectiveController(ObjectiveService objectiveService) {
        this.objectiveService = objectiveService;
    }

    @GetMapping
    public List<Objective> getAllObjectives(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return objectiveService.getAllObjectives(currentUser.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Objective> getObjectiveById(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Objective objective = objectiveService.getObjectiveById(id, currentUser.getId());
        return ResponseEntity.ok(objective);
    }


    @PostMapping
    public Objective createObjective(@RequestBody Objective objective) {
        return objectiveService.saveObjective(objective);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateObjective(@RequestBody Objective objective, @PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        objective.setId(id);
        objectiveService.updateObjective(objective, currentUser.getId());
        return ResponseEntity.ok("Objective with id " + id + " has been updated.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteObjective(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        objectiveService.deleteObjective(id, currentUser.getId());
        return ResponseEntity.ok("Objective with id " + id + " has been deleted.");
    }


    @PostMapping("/assign/{ownerId}/{objectiveId}")
    public Objective assignObjectiveToCollaborator(
            @AuthenticationPrincipal UserDetailsImpl currentUser,  // Assuming you're using Spring Security's UserDetails for authentication
            @PathVariable Long ownerId,
            @PathVariable Long objectiveId,
            @RequestBody ObjectiveUpdateDto percentage
    ) {
        return objectiveService.assignObjectiveToCollaborator(currentUser.getId(), ownerId, objectiveId, percentage.getPercentage());
    }






}


