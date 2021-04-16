package com.nvfredy.springangularappbackend.controller;

import com.nvfredy.springangularappbackend.dto.UserDTO;
import com.nvfredy.springangularappbackend.entity.User;
import com.nvfredy.springangularappbackend.facade.UserFacade;
import com.nvfredy.springangularappbackend.service.UserService;
import com.nvfredy.springangularappbackend.validators.ResponseErrorValidation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;
    private final ResponseErrorValidation errorValidation;
    private final UserFacade userFacade;

    public UserController(UserService userService, ResponseErrorValidation errorValidation, UserFacade userFacade) {
        this.userService = userService;
        this.errorValidation = errorValidation;
        this.userFacade = userFacade;
    }

    @GetMapping("/")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        User user = userService.getCurrentUser(principal);
        UserDTO userDTO = userFacade.userToUserDTO(user);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable("userId") String userId) {
        User user = userService.getUserById(Long.parseLong(userId));
        UserDTO userDTO = userFacade.userToUserDTO(user);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDTO userDTO,
                                             Principal principal,
                                             BindingResult bindingResult) {
        ResponseEntity<Object> errors = errorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        User user = userService.updateUser(userDTO, principal);

        UserDTO updatedUser = userFacade.userToUserDTO(user);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}
