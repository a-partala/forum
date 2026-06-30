package net.partala.forum.realm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateRealmRequest (

        @NotBlank
        @Size(min = 3, max = 100)
        @Pattern(
                regexp = "^[a-zA-Z_]+$",
                message = "Only latin letters and underscores are allowed"
        )
        String name,
        @Size(max = 1000)
        String description,
        @NotNull
        Long ownerId,
        Long parentId
) {
}
