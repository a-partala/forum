package net.partala.forum.auth.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationEntryPoint authEntryPoint;
    @InjectMocks
    private JwtAuthenticationFilter authFilter;

    @Test
    void doFilterInternal_SkipParsingToken_WhenHeaderIsNull() throws ServletException, IOException {
        when(request.getHeader(any())).thenReturn(null);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(any(), any());
        verify(jwtService, times(0)).parseAllClaims(any());
        verify(authEntryPoint, times(0)).commence(any(), any(), any());
    }

    @Test
    void doFilterInternal_SkipParsingToken_WhenBlankHeader() throws ServletException, IOException {
        when(request.getHeader(any())).thenReturn("");
        when(jwtService.startsWithTargetType(any())).thenReturn(false);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(any(), any());
        verify(jwtService, times(0)).parseAllClaims(any());
        verify(authEntryPoint, times(0)).commence(any(), any(), any());
    }

    @Test
    void doFilterInternal_SkipParsingToken_WhenIncorrectTokenType() throws ServletException, IOException {
        when(request.getHeader(any())).thenReturn("token");
        when(jwtService.startsWithTargetType(any())).thenReturn(false);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(any(), any());
        verify(jwtService, times(0)).parseAllClaims(any());
        verify(authEntryPoint, times(0)).commence(any(), any(), any());
    }

    @Test
    void doFilterInternal_Commence_WhenPurposeIsNotAccess() throws ServletException, IOException {
        when(request.getHeader(any())).thenReturn("token");
        when(jwtService.startsWithTargetType(any())).thenReturn(true);
        when(jwtService.extractPurpose(any())).thenReturn(TokenPurpose.VERIFY_EMAIL);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(0)).doFilter(any(), any());
        verify(authEntryPoint, times(1)).commence(any(), any(), any());
    }

    @Test
    void doFilterInternal_Authenticate_WhenPurposeIsAccess() throws ServletException, IOException {
        Claims tokenClaims = mock();
        when(tokenClaims.getSubject()).thenReturn("user");
        when(request.getHeader(any())).thenReturn("token");
        when(jwtService.startsWithTargetType(any())).thenReturn(true);
        when(jwtService.parseAllClaims(any())).thenReturn(tokenClaims);
        when(jwtService.extractPurpose(any())).thenReturn(TokenPurpose.ACCESS);
        when(jwtService.extractAuthorities(any())).thenReturn(new HashSet<>());

        authFilter.doFilterInternal(request, response, filterChain);

        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }
}