package net.partala.forum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import net.partala.forum.comment.CommentEntity;

public record CommentContentRequest(
        @NotBlank
        @Size(max = 1000)
        String content
) {

    public boolean isSameData(CommentEntity entity) {
        return content.equals(entity.getContent());
    }
}
