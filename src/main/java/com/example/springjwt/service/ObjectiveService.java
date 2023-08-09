package com.example.springjwt.service;

import com.example.springjwt.exception.NoObjectivesFoundException;
import com.example.springjwt.exception.ObjectiveNotFoundException;
import com.example.springjwt.exception.UserNotFoundException;
import com.example.springjwt.models.User;
import com.example.springjwt.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.springjwt.models.Objective;
import com.example.springjwt.repository.ObjectiveRepository;
import org.springframework.security.access.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

@Service
public class ObjectiveService {

    private final ObjectiveRepository objectiveRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(ObjectiveService.class);



    @Autowired
     public ObjectiveService(ObjectiveRepository objectiveRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.objectiveRepository = objectiveRepository;
     }


    public List<Objective> getAllObjectives(Long requesterId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException(requesterId));

        List<Objective> objectives;

        if (isUserAManager(requester)) {
            objectives = objectiveRepository.findAll();
        } else {
            objectives = objectiveRepository.findByOwner(requester);
        }

        if (objectives.isEmpty()) {
            throw new NoObjectivesFoundException("No objectives found for user with id: " + requesterId);

        }

        return objectives;
     }


     public Objective getObjectiveById(Long id, Long requesterId) {
        Objective existingObjective = objectiveRepository.findById(id)
                .orElseThrow(() -> new ObjectiveNotFoundException("Objective not found with id "  + id));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException(requesterId));

        if (requester.equals(existingObjective.getOwner()) || isUserAManager(requester)) {
            return existingObjective;
        } else {
            throw new AccessDeniedException("Requester does not have permission to view this objective");
        }
     }

      public Objective saveObjective(Objective objective) {
        //User owner = userRepository.findById(objective.getOwner().getId())
        //        .orElseThrow(() -> new EntityNotFoundException("User not found"));

       // objective.setOwner(owner);

        return objectiveRepository.save(objective);
      }

       public ResponseEntity<String> deleteObjective(Long id, Long ownerId) {
            Objective existingObjective = objectiveRepository.findById(id)
                    .orElseThrow(() -> new ObjectiveNotFoundException("Objective not found with id " + id));

            User realOwner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new UserNotFoundException(ownerId));

            if (realOwner.equals(existingObjective.getOwner()) || isUserAManager(realOwner)) {
                objectiveRepository.deleteById(id);
                return ResponseEntity.ok("Objective with id " + id + " has been deleted.");
            } else {
                throw new AccessDeniedException("User does not have permission to delete this objective");
            }

         }

       public ResponseEntity<String> updateObjective(Objective updatedObjective, Long ownerId) {
          Objective existingObjective = objectiveRepository.findById(updatedObjective.getId())
                .orElseThrow(() -> new ObjectiveNotFoundException("Objective not found with id:"+ updatedObjective.getId()));

          User realOwner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));

          if (realOwner.equals(existingObjective.getOwner()) || isUserAManager(realOwner)) {
            existingObjective.setDescription(updatedObjective.getDescription());
            existingObjective.setStatus(updatedObjective.getStatus());
            existingObjective.setDeadline(updatedObjective.getDeadline());

            objectiveRepository.save(existingObjective);

            return ResponseEntity.ok("Objective with id " + updatedObjective.getId() + " has been updated.");
          } else {
            throw new AccessDeniedException("User does not have permission to update this objective");
          }

       }

    private boolean isUserAManager(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getId() == 3); // 3 est l'ID du rôle manager
    }

    private boolean isUserACollaborator(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getId() == 2);
    }


    public Objective assignObjectiveToCollaborator(Long requesterId, Long ownerId, Long objectiveId, double percentage) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException(requesterId));

        if (!isUserAManager(requester)) {
            throw new AccessDeniedException("User does not have permission to assign objectives");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));
        Objective objective = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new ObjectiveNotFoundException("Objective not found with id:"+objectiveId));

        if (!isUserACollaborator(owner)) {
            throw new AccessDeniedException("The specified user is not a collaborator");
        }

        objective.setPercentage(percentage);
        objective.setOwner(owner);

        return objectiveRepository.save(objective);
    }





}




