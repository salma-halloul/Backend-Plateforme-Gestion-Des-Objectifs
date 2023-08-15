package com.example.springjwt.service;
import com.example.springjwt.exception.NoNotificationsFoundException;
import com.example.springjwt.models.NotificationDTO;
import com.example.springjwt.models.NotificationResponseDTO;
import com.example.springjwt.models.User;
import com.example.springjwt.repository.UserRepository;
import com.example.springjwt.security.services.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.springjwt.models.Notification;
import com.example.springjwt.repository.NotificationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;



import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;


    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;

    }

    public List<NotificationDTO> getAllNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long currentUserId = userDetails.getId();

        List<Notification> userNotifications = notificationRepository.findByUserId(currentUserId);

        if (userNotifications.isEmpty()) {
            throw new NoNotificationsFoundException("No notifications found for user with ID: " + currentUserId);
        }

        return userNotifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setCreationDate(notification.getCreationDate());
        return dto;
    }




    public NotificationResponseDTO getNotificationById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long currentUserId = userDetails.getId();

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NoNotificationsFoundException("No notification found for ID: " + id));

        if (!notification.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to view this notification");
        }

        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setId(notification.getId());
        response.setMessage(notification.getMessage());
        response.setCreationDate(notification.getCreationDate());
        response.setOwnerUsername(notification.getUser().getUsername());
        response.setRelatedObjective(notification.getRelatedObjective());

        return response;
    }


    public Notification saveNotification(Notification notification) {
        User user = userRepository.findById(notification.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        notification.setUser(user);

        return notificationRepository.save(notification);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}