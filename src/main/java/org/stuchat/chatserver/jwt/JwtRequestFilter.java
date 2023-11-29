package org.stuchat.chatserver.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.stuchat.chatserver.service.UserService;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            String username;
            String jwt = parseJwt(authHeader);
            if (jwtUtils.validateJwtToken(jwt)) {
                username = jwtUtils.getUsername(jwt);
                UserDetails userDetails = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken UPA_token = new
                        UsernamePasswordAuthenticationToken(userDetails,
                        null,
                        jwtUtils.getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                );
                SecurityContextHolder.getContext().setAuthentication(UPA_token);
            }
        } catch (MalformedJwtException malformedJwtException) {
            log.debug("Malformed token: "+malformedJwtException);
        } catch (ExpiredJwtException expiredJwtException) {
            log.debug("Expired token: "+ expiredJwtException);
        } catch (SignatureException signatureException) {
            log.debug("Token with wrong signature: "+signatureException);
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(String authHeader) throws MalformedJwtException {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        } throw new MalformedJwtException("Wrong JWT");
    }
}
