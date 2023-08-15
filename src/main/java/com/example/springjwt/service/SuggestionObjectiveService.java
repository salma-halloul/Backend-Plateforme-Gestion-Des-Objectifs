package com.example.springjwt.service;

import com.example.springjwt.exception.*;
import com.example.springjwt.models.*;
import com.example.springjwt.repository.NotificationRepository;
import com.example.springjwt.repository.SuggestionObjectiveRepository;
import com.example.springjwt.repository.UserRepository;
import com.example.springjwt.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;



import java.util.List;

@Service
public class SuggestionObjectiveService {

    private final SuggestionObjectiveRepository suggestionObjectiveRepository;
    private final UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;


    @Autowired
    public SuggestionObjectiveService(SuggestionObjectiveRepository suggestionObjectiveRepository, UserRepository userRepository) {
        this.suggestionObjectiveRepository = suggestionObjectiveRepository;
        this.userRepository = userRepository;
    }

    public List<SuggestionObjective> getAllSuggestions() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = userDetails.getId();
        boolean isManager = userDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_MANAGER"));

        List<SuggestionObjective> suggestions;

        if (isManager) {
            suggestions = suggestionObjectiveRepository.findAll();
        } else {
            suggestions = suggestionObjectiveRepository.findBySuggestedById(currentUserId);

            if (suggestions.isEmpty()) {
                throw new NoSuggestionFoundException(currentUserId);
            }
        }

        return suggestions;
    }


    public SuggestionObjective getSuggestionById(Long id, UserDetailsImpl userPrincipal) {
        SuggestionObjective suggestion = suggestionObjectiveRepository.findById(id)
                .orElseThrow(() -> new SuggestionNotFoundException(id));

        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UserNotFoundException(userPrincipal.getId()));

        if (!(currentUser.equals(suggestion.getSuggestedBy()) ||
                currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_MANAGER)))) {
            throw new SuggestionNotBelongingException(currentUser.getId(), suggestion.getId());
        }

        return suggestion;
    }




    public SuggestionObjective saveSuggestion(SuggestionObjective suggestion, UserDetailsImpl userPrincipal) {

        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UserNotFoundException(userPrincipal.getId()));

        // Check if the user has the COLLABORATOR role
        if (currentUser.getRoles().stream().noneMatch(role -> role.getName().equals(ERole.ROLE_COLLABORATER))) {
            throw new AccessDeniedException("User does not have permission to suggest this objective");
        }

        suggestion.setStatus(EStatusSug.PENDING);
        suggestion.setSuggestedBy(currentUser);

        // First save the suggestion to the repository
        SuggestionObjective savedSuggestion = suggestionObjectiveRepository.save(suggestion);

        // Then notify the managers
        List<User> managers = userRepository.findAllByRoles_Name(ERole.ROLE_MANAGER);
        notifyAllManagersAboutNewSuggestion(savedSuggestion, managers, userPrincipal.getUsername());

        return savedSuggestion;
    }

    private void notifyAllManagersAboutNewSuggestion(SuggestionObjective suggestionObjective,List<User> managers, String collaboratorUsername) {
        String notificationMessage = collaboratorUsername + " has suggested a new objective.";
        for (User manager : managers) {
            Notification notification = new Notification();
            notification.setMessage(notificationMessage);
            notification.setUser(manager); // The manager is the recipient
            notification.setRelatedSuggestion(suggestionObjective);
            notificationRepository.save(notification);
        }
    }


    public SuggestionObjective approveOrRejectSuggestion(Long suggestionId, boolean approve, UserDetailsImpl userPrincipal) {

        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UserNotFoundException(userPrincipal.getId()));

        // Vérifie si l'utilisateur a le rôle MANAGER
        if (currentUser.getRoles().stream().noneMatch(role -> role.getName().equals(ERole.ROLE_MANAGER))) {
            throw new AccessDeniedException("You do not have the permission to approve or reject suggestions");
        }

        SuggestionObjective suggestion = suggestionObjectiveRepository.findById(suggestionId)
                .orElseThrow(() -> new SuggestionNotFoundException(suggestionId));

        suggestion.setStatus(approve ? EStatusSug.APPROVED : EStatusSug.REJECTED);
        suggestion.setManager(currentUser);

        // Envoyer une notification au collaborateur concernant la décision du manager
        notifyCollaboratorAboutDecision(currentUser, suggestion, approve);

        return suggestionObjectiveRepository.save(suggestion);
    }

    private void notifyCollaboratorAboutDecision(User manager, SuggestionObjective suggestion, boolean approve) {
        String notificationMessage = "Your suggestion has been " + (approve ? "approved" : "rejected") + " by " + manager.getUsername() + ".";
        Notification notification = new Notification();
        notification.setMessage(notificationMessage);
        notification.setUser(suggestion.getSuggestedBy()); // Le collaborateur est le destinataire
        notification.setRelatedSuggestion(suggestion);
        notificationRepository.save(notification);
    }



    public void deleteSuggestion(Long suggestionId, UserDetailsImpl userPrincipal) {
        SuggestionObjective suggestion = suggestionObjectiveRepository.findById(suggestionId)
                .orElseThrow(() -> new SuggestionNotFoundException(suggestionId));

        User currentUser = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UserNotFoundException(userPrincipal.getId()));

        // Vérifier si l'utilisateur est le créateur de la suggestion ou s'il est un manager
        if (!(currentUser.equals(suggestion.getSuggestedBy()) ||
                currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_MANAGER)))) {
            throw new AccessDeniedException("You don't have the permission to delete this suggestion");
        }

        suggestionObjectiveRepository.deleteById(suggestionId);
    }

}


