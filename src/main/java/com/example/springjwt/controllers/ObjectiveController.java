package com.example.springjwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.springjwt.models.Objective;
import com.example.springjwt.service.ObjectiveService;

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
    public List<Objective> getAllObjectives() {
        return objectiveService.getAllObjectives();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Objective> getObjectiveById(@PathVariable Long id) {
        return objectiveService.getObjectiveById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Objective createObjective(@RequestBody Objective objective) {
        return objectiveService.saveObjective(objective);
    }

    @PutMapping("/{id}")
    public Objective updateObjective(@RequestBody Objective objective, @PathVariable Long id) {
        objective.setId(id);
        return objectiveService.saveObjective(objective);
    }

    @DeleteMapping("/{id}")
    public void deleteObjective(@PathVariable Long id) {
        objectiveService.deleteObjective(id);
    }
}

