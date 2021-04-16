package com.nvfredy.springangularappbackend.service;

import com.nvfredy.springangularappbackend.dto.UserDTO;
import com.nvfredy.springangularappbackend.entity.User;
import com.nvfredy.springangularappbackend.entity.enums.Role;
import com.nvfredy.springangularappbackend.exceptions.UserExistsException;
import com.nvfredy.springangularappbackend.payload.request.SignUpRequest;
import com.nvfredy.springangularappbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserService {
    public static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(SignUpRequest sign) {
        try {
            User user = new User();
            user.setEmail(sign.getEmail());
            user.setUsername(sign.getUsername());
            user.setFirstName(sign.getFirstName());
            user.setLastName(sign.getLastName());
            user.setPassword(passwordEncoder.encode(sign.getPassword()));
            user.getRoles().add(Role.ROLE_USER);

            LOGGER.info("Saving user {}", sign.getEmail());
            return userRepository.save(user);
        } catch (Exception e) {
            LOGGER.error("Error during registration {}", e.getMessage());
            throw new UserExistsException("User " + sign.getUsername() + " already exist.");
        }
    }

    public User updateUser(UserDTO userDTO, Principal principal) {
        User user = findUserByPrincipal(principal);
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        userDTO.setBio(userDTO.getBio());
        return userRepository.save(user);
    }

    private User findUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with username: " + username + " not found."));
    }

    public User getCurrentUser(Principal principal) {
        return findUserByPrincipal(principal);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(
                "User not found."
        ));
    }
}
