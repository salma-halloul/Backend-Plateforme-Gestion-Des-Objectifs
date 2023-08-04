package com.example.springjwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.springjwt.models.Objective;
import com.example.springjwt.repository.ObjectiveRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ObjectiveService {

    private final ObjectiveRepository objectiveRepository;

    @Autowired
    public ObjectiveService(ObjectiveRepository objectiveRepository) {
        this.objectiveRepository = objectiveRepository;
    }

    public List<Objective> getAllObjectives() {
        return objectiveRepository.findAll();
    }

    public Optional<Objective> getObjectiveById(Long id) {
        return objectiveRepository.findById(id);
    }

    public Objective saveObjective(Objective objective) {
        return objectiveRepository.save(objective);
    }

    public void deleteObjective(Long id) {
        objectiveRepository.deleteById(id);
    }
}

