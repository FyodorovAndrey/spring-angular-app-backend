package com.nvfredy.springangularappbackend.controller;

import com.nvfredy.springangularappbackend.payload.request.LoginRequest;
import com.nvfredy.springangularappbackend.payload.request.SignUpRequest;
import com.nvfredy.springangularappbackend.payload.response.JWTTokenSuccessResponse;
import com.nvfredy.springangularappbackend.payload.response.MessageResponse;
import com.nvfredy.springangularappbackend.security.JWTTokenProvider;
import com.nvfredy.springangularappbackend.security.SecurityConstants;
import com.nvfredy.springangularappbackend.service.UserService;
import com.nvfredy.springangularappbackend.validators.ResponseErrorValidation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("api/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    private final JWTTokenProvider provider;
    private final UserService userService;
    private final ResponseErrorValidation errorValidation;
    private final AuthenticationManager authenticationManager;

    public AuthController(JWTTokenProvider provider, UserService userService, ResponseErrorValidation errorValidation, AuthenticationManager authenticationManager) {
        this.provider = provider;
        this.userService = userService;
        this.errorValidation = errorValidation;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signin")
    public ResponseEntity<Object> userAuthentication(@Valid @RequestBody LoginRequest loginRequest,
                                                     BindingResult bindingResult) {
        ResponseEntity<Object> errors = errorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = SecurityConstants.TOKEN_PREFIX + provider.generateToken(authentication);

        return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));

    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpRequest signUpRequest,
                                               BindingResult bindingResult) {


        ResponseEntity<Object> errors = errorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }
        userService.createUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("New user registered successfully"));
    }

}
