package by.testtask.balancehub.conf.auth;

import by.testtask.balancehub.exceptions.UnauthorizedException;
import by.testtask.balancehub.services.impl.JwtProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static by.testtask.balancehub.utils.Constants.TOKEN_HEADER;
import static by.testtask.balancehub.utils.Constants.TOKEN_TYPE;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Value("${spring.app.web.ignoredUrls:*}")
    private List<String> ignoredUrls;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String path = request.getRequestURI();
        log.debug("path: {}", path);
        log.debug("ignoredUrls: {}", ignoredUrls);
        log.debug("shouldNotFilter: {}", ignoredUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, path)));
        return ignoredUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader(TOKEN_HEADER);
            if (StringUtils.isBlank(header) || !StringUtils.startsWith(header, TOKEN_TYPE)) {
                log.debug("No JWT token found in request header or invalid token type. Skipping authentication.");
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = header.substring(TOKEN_TYPE.length());
            if (!jwtProvider.validateAccessToken(jwt)) {
                log.warn("Invalid JWT token detected. Unauthorized access attempt.");
                throw new UnauthorizedException();
            }

            String username = jwtProvider.getAccessClaims(jwt).getSubject();
            if (StringUtils.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("Authenticated user '{}'. Setting security context.", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            } else {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (!auth.getPrincipal().equals(username)) {
                    log.error("JWT token belongs to a different user. Invalid token for user '{}'.", username);
                    throw new JwtException("Invalid token");
                }
            }

            filterChain.doFilter(request, response);

        } catch (UnauthorizedException | JwtException e) {
            log.error("Authentication failed: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print(e.getMessage());
        }
    }
}