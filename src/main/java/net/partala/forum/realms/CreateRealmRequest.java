package net.partala.forum.realms;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRealmRequest (

        @Size(min = 3, max = 100)
        String name,
        @Size(max = 1000)
        String description,
        @NotNull
        Long ownerId,
        Long parentId
) {
}
