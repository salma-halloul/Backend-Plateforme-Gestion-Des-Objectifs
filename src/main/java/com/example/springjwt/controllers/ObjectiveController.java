package com.example.springjwt.controllers;

import com.example.springjwt.models.ObjectiveUpdateDto;
import com.example.springjwt.models.ObjectiveWithOwnerDTO;
import com.example.springjwt.models.ShareObjectiveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.springjwt.models.Objective;
import com.example.springjwt.service.ObjectiveService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.springjwt.security.services.UserDetailsImpl;


import java.util.List;
import java.util.stream.Collectors;

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
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return objectiveService.saveObjective(objective, userDetails);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateObjective(@RequestBody Objective objective, @PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        objective.setId(id);
        return objectiveService.updateObjective(objective, currentUser.getId());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteObjective(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        objectiveService.deleteObjective(id, currentUser.getId());
        return ResponseEntity.ok("Objective with id " + id + " has been deleted.");
    }


    @PostMapping("/assign/{ownerId}/{objectiveId}")
    public Objective assignObjectiveToCollaborator(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PathVariable Long ownerId,
            @PathVariable Long objectiveId,
            @RequestBody ObjectiveUpdateDto percentage
    ) {
        return objectiveService.assignObjectiveToCollaborator(currentUser.getId(), ownerId, objectiveId, percentage.getPercentage());
    }

    @PostMapping("/share")
    public ResponseEntity<Void> shareObjective(@RequestBody ShareObjectiveDTO shareObjectiveDTO, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        objectiveService.shareObjective(shareObjectiveDTO, currentUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/shared-with-me")
    public ResponseEntity<List<ObjectiveWithOwnerDTO>> getSharedObjectives(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<Objective> objectives = objectiveService.getSharedObjectivesForUser(currentUser.getId());
        List<ObjectiveWithOwnerDTO> response = objectives.stream()
                .map(obj -> new ObjectiveWithOwnerDTO(obj, obj.getOwner().getUsername()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }




}


