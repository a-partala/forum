package net.partala.forum.auth.jwt;

import java.time.Instant;

public record JwtResponse(
        String token,
        Instant expiresAt
) {
}
