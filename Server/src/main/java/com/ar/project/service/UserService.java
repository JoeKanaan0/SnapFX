package com.ar.project.service;

import com.ar.project.exception.ResourceNotFoundException;
import com.ar.project.model.Image;
import com.ar.project.model.User;
import com.ar.project.repository.ImageRepository;
import com.ar.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public List<User> getAllUsersExcept(Long excludedId) {
        return userRepository.findAll().stream()
                .filter(user -> !(user.getId() == excludedId))
                .collect(Collectors.toList());
    }


    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(String name, String password) {
        return userRepository.findByNameAndPassword(name, password);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist"));

        user.setName(userDetails.getName());
        user.setPassword(userDetails.getPassword());
        user.setProfile(userDetails.getProfile());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}