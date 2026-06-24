package net.partala.forum.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest (

        @NotBlank
        @Size(max = 255)
        String login,

        @NotBlank
        @Size(min = 8, max = 64)
        String password
){
}
