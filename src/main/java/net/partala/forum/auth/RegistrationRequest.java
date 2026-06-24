package net.partala.forum.auth;

import jakarta.validation.constraints.*;
import net.partala.forum.auth.annotation.EmailValidation;
import net.partala.forum.auth.annotation.UsernameValidation;

public record RegistrationRequest (

        @UsernameValidation
        String username,

        @EmailValidation
        String email,

        @NotBlank
        @Size(min = 8, max = 64)
        String password
){
}
