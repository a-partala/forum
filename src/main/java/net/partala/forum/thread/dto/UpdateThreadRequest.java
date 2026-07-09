package net.partala.forum.thread.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import net.partala.forum.thread.ThreadEntity;

public record UpdateThreadRequest(

        @NotBlank
        @Size(max = 100)
        String title,
        @NotBlank
        @Size(max = 4000)
        String content
) {

    public boolean isSameData(ThreadEntity entity) {

        return entity.getTitle().equals(title) &&
                entity.getContent().equals(content);
    }
}
