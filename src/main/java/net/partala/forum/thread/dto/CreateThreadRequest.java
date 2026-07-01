package net.partala.forum.thread.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateThreadRequest(

        @NotBlank
        @Size(max = 100)
        String title,
        @NotBlank
        @Size(max = 4000)
        String content,
        @NotNull
        Long realmId
) {
}
