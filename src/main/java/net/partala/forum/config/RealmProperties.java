package net.partala.forum.config;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.realm")
@Validated
public record RealmProperties(
        @Positive
        int maxDepth
) {
}