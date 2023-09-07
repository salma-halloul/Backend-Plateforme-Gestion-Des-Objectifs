package com.example.springjwt.service;
import com.example.springjwt.repository.UserRepository;
import com.example.springjwt.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAllCollaborators() {
        return userRepository.findAllByRoles_Name(ERole.ROLE_COLLABORATER);
    }
    

    
}

