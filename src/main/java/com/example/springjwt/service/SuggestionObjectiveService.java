package com.example.springjwt.service;

import com.example.springjwt.exception.*;
import com.example.springjwt.models.ERole;
import com.example.springjwt.models.EStatusSug;
import com.example.springjwt.models.SuggestionObjective;
import com.example.springjwt.models.User;
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

        return suggestionObjectiveRepository.save(suggestion);
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

        return suggestionObjectiveRepository.save(suggestion);
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


