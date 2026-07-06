package net.partala.forum.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.partala.forum.auth.UserPrincipalService;
import net.partala.forum.user.AccountStatus;
import net.partala.forum.user.UserContext;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthenticationEntryPoint authEntryPoint;
    private static final String AUTHORIZATION_HEADER = "Authorization";

    public JwtAuthenticationFilter(JwtService jwtService,
                                   AuthenticationEntryPoint authEntryPoint) {
        this.jwtService = jwtService;
        this.authEntryPoint = authEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String rawToken = request.getHeader(AUTHORIZATION_HEADER);

        if(rawToken == null || !jwtService.startsWithTargetType(rawToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var token = jwtService.trimPrefix(rawToken);
            Claims tokenClaims = jwtService.parseAllClaims(token);
            var status = jwtService.extractStatus(tokenClaims);

            if(!jwtService.extractPurpose(tokenClaims).equals(TokenPurpose.ACCESS)) {
                throw new BadCredentialsException("Invalid token purpose");
            }

            var username = tokenClaims.getSubject();
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var auth = new UsernamePasswordAuthenticationToken(
                        new UserContext(
                                jwtService.extractId(tokenClaims),
                                jwtService.extractRole(tokenClaims),
                                status),
                        null,
                        jwtService.extractAuthorities(tokenClaims)
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (ExpiredJwtException e) {
            authEntryPoint.commence(request, response, new CredentialsExpiredException("Token expired", e));
            return;
        } catch (BadCredentialsException | DisabledException e) {
            authEntryPoint.commence(request, response, e);
            return;
        } catch (JwtException | IllegalArgumentException e) {
            authEntryPoint.commence(request, response, new BadCredentialsException("Invalid token", e));
            return;
        }

        filterChain.doFilter(request, response);
    }
}