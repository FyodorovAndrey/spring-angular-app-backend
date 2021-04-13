package com.nvfredy.springangularappbackend.validators;

import com.nvfredy.springangularappbackend.annotations.PasswordMatches;
import com.nvfredy.springangularappbackend.payload.request.SignUpRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomPasswordMatching implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        SignUpRequest signUpRequest = (SignUpRequest) o;
        return signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword());
    }
}
