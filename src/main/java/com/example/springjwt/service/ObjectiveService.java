package com.example.springjwt.service;

import com.example.springjwt.exception.NoObjectivesFoundException;
import com.example.springjwt.exception.ObjectiveNotFoundException;
import com.example.springjwt.exception.UserNotFoundException;
import com.example.springjwt.models.*;
import com.example.springjwt.repository.NotificationRepository;
import com.example.springjwt.repository.UserRepository;
import com.example.springjwt.security.services.UserDetailsImpl;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.springjwt.repository.ObjectiveRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class ObjectiveService {

    private final ObjectiveRepository objectiveRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(ObjectiveService.class);

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;

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
                .orElseThrow(() -> new ObjectiveNotFoundException("Objective not found with id " + id));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException(requesterId));

        if (requester.equals(existingObjective.getOwner()) || isUserAManager(requester)) {
            return existingObjective;
        } else {
            throw new AccessDeniedException("Requester does not have permission to view this objective");
        }
    }

    public Objective saveObjective(Objective objective, UserDetailsImpl userPrincipal) {

        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UserNotFoundException(userPrincipal.getId()));

        // Check if the user has the MANAGER role
        if (currentUser.getRoles().stream().noneMatch(role -> role.getName().equals(ERole.ROLE_MANAGER))) {
            throw new AccessDeniedException("User does not have permission to creat this objective");
        }

        objective.setOwner(currentUser);
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

    public ResponseEntity<String> updateObjective(Objective updatedObjective, Long userId) {
        Objective existingObjective = objectiveRepository.findById(updatedObjective.getId())
                .orElseThrow(() -> new ObjectiveNotFoundException(
                        "Objective not found with id:" + updatedObjective.getId()));

        User authenticatedUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (authenticatedUser.equals(existingObjective.getOwner())) {
            // Si c'est le collaborateur, il ne peut changer que le statut
            if (!existingObjective.getStatus().equals(updatedObjective.getStatus())) {
                // Informer le manager (assignedBy) du changement de statut
                User assignedManager = existingObjective.getAssignedBy();
                if (assignedManager != null) {
                    createStatusChangeNotification(assignedManager, existingObjective, updatedObjective.getStatus());
                }
                existingObjective.setStatus(updatedObjective.getStatus());
            } else {
                return ResponseEntity.badRequest().body("You can only change the objective status.");
            }
        } else if (authenticatedUser.equals(existingObjective.getAssignedBy())) {
            // Si c'est le manager, il peut changer tous les champs
            existingObjective.setDescription(updatedObjective.getDescription());
            existingObjective.setStatus(updatedObjective.getStatus());
            existingObjective.setDeadline(updatedObjective.getDeadline());
        } else {
            throw new AccessDeniedException("You do not have permission to update this objective.");
        }

        objectiveRepository.save(existingObjective);

        return ResponseEntity.ok("Objective with id " + updatedObjective.getId() + " has been updated.");
    }

    private void createStatusChangeNotification(User manager, Objective objective, EStatus newStatus) {
        String message = "The status of Objective with ID: " + objective.getId() + " has been changed to: " + newStatus;
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUser(manager); // Assigned manager is the recipient
        notificationRepository.save(notification);
    }

    private boolean isUserAManager(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getId() == 3); // 3 est l'ID du rôle manager
    }

    private boolean isUserACollaborator(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getId() == 2);
    }

    public Objective assignObjectiveToCollaborator(Long requesterId, Long ownerId, Long objectiveId,
            double percentage) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException(requesterId));

        if (!isUserAManager(requester)) {
            throw new AccessDeniedException("User does not have permission to assign objectives");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));
        Objective objective = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new ObjectiveNotFoundException("Objective not found with id:" + objectiveId));

        if (!isUserACollaborator(owner)) {
            throw new AccessDeniedException("The specified user is not a collaborator");
        }

        objective.setPercentage(percentage);
        objective.setOwner(owner);
        objective.setAssignedBy(requester); // 'requester' est le manager assignant l'objectif

        // Créez une notification après avoir attribué l'objectif
        createNotificationForObjectiveAssignment(requester, owner, objective);

        return objectiveRepository.save(objective);
    }

    private void createNotificationForObjectiveAssignment(User manager, User collaborator, Objective objective) {
        String message = manager.getUsername() + " assigned you a new objective.";
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setUser(collaborator); // collaborator is the recipient
        notification.setRelatedObjective(objective);
        notificationRepository.save(notification);
    }

    public void shareObjective(ShareObjectiveDTO shareObjectiveDTO, UserDetailsImpl currentUser) {
        Objective objective = objectiveRepository.findById(shareObjectiveDTO.getObjectiveId())
                .orElseThrow(() -> new ObjectiveNotFoundException(
                        "Objective with ID: " + shareObjectiveDTO.getObjectiveId() + " not found"));

        if (!objective.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the owner can share this objective");
        }

        List<User> usersToShareWith = userRepository.findAllById(shareObjectiveDTO.getUserIds());

        for (User user : usersToShareWith) {
            if (!isUserACollaborator(user)) {
                throw new AccessDeniedException("You can only share objectives with collaborators");
            }

            Notification notification = new Notification();
            notification.setUser(user);
            notification.setMessage(currentUser.getUsername() + " has shared an objective with you.");
            notificationService.saveNotification(notification);
        }

        objective.getSharedWith().addAll(usersToShareWith);
        objectiveRepository.save(objective);
    }

    public List<Objective> getSharedObjectivesForUser(Long userId) {
        return objectiveRepository.findBySharedWith_Id(userId);
    }

    // Count number of statut for each user
    public Map<EStatus, Long> getStatusCountForUser(Long userId) {
        List<Objective> objectives = getAllObjectives(userId);

        Map<EStatus, Long> statusCount = objectives.stream()
                .collect(Collectors.groupingBy(Objective::getStatus, Collectors.counting()));

        return statusCount;
    }

    // Objective's deadline for each user
    public Map<Long, ObjectiveDetails> getDetailsForUser(Long userId) {
        List<Objective> objectives = getAllObjectives(userId);

        Map<Long, ObjectiveDetails> detailsMap = objectives.stream()
                .collect(Collectors.toMap(
                        Objective::getId,
                        obj -> new ObjectiveDetails(obj.getDescription(), obj.getDeadline())));

        return detailsMap;
    }

}
