package net.partala.forum.auth.jwt.dto;

import java.time.Instant;

public record JwtResponse(
        String token,
        Instant expiresAt
) {
}
