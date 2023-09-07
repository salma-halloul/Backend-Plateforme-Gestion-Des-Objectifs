package com.example.springjwt.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.example.springjwt.models.User;
import com.example.springjwt.service.UserService;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService UserService;

    @GetMapping("/collaborators")
    public List<User> getAllCollaborators() {
        return UserService.findAllCollaborators();
    }
}

