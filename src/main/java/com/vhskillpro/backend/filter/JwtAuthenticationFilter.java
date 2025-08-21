package com.vhskillpro.backend.filter;

import com.vhskillpro.backend.modules.auth.BlacklistTokenRepository;
import com.vhskillpro.backend.modules.user.CustomUserDetails;
import com.vhskillpro.backend.modules.user.UserService;
import com.vhskillpro.backend.utils.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserService userService;
  private final BlacklistTokenRepository blacklistTokenRepository;

  public JwtAuthenticationFilter(
      JwtService jwtService,
      UserService userService,
      BlacklistTokenRepository blacklistTokenRepository) {
    this.jwtService = jwtService;
    this.userService = userService;
    this.blacklistTokenRepository = blacklistTokenRepository;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain chain)
      throws ServletException, IOException {
    String accessToken = extractTokenFromHeader(request);

    if (accessToken != null
        && jwtService.isValidToken(accessToken)
        && !blacklistTokenRepository.existsById(accessToken)
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      // Get email from token
      String email = jwtService.getPayload(accessToken).get("email", String.class);

      // Get user details from the database
      CustomUserDetails userDetails = null;
      try {
        userDetails = userService.loadUserByUsername(email);
      } catch (Exception e) {
        System.err.println(e);
        chain.doFilter(request, response);
        return;
      }

      if (userDetails != null) {
        SecurityContextHolder.getContext()
            .setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()));
      }
    }

    chain.doFilter(request, response);
  }

  private String extractTokenFromHeader(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }
}
