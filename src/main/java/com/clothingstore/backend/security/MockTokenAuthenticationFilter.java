package com.clothingstore.backend.security;

import com.clothingstore.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Recognizes access tokens issued by {@link com.clothingstore.backend.service.impl.AuthServiceImpl}
 * ({@code mock-token-&lt;userId&gt;}) and sets the security context so Spring Security
 * {@code authenticated()} and {@code hasRole} work.
 */
@Component
@RequiredArgsConstructor
public class MockTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String MOCK_TOKEN_PREFIX = "mock-token-";

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty() || "undefined".equals(token) || !token.startsWith(MOCK_TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = token.substring(MOCK_TOKEN_PREFIX.length());
        userRepository.findById(userId).ifPresent(user -> {
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
            var authentication = new UsernamePasswordAuthenticationToken(
                    user.getId(),
                    null,
                    authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });

        filterChain.doFilter(request, response);
    }
}
