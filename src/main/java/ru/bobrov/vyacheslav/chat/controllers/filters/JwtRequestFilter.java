package ru.bobrov.vyacheslav.chat.controllers.filters;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.bobrov.vyacheslav.chat.configurations.JwtTokenUtil;
import ru.bobrov.vyacheslav.chat.services.JwtUserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.services.Constants.AUTHORIZATION_HEADER;
import static ru.bobrov.vyacheslav.chat.services.Constants.TOKEN_PREFIX;

@Component
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    static final Pattern TOKEN_PATTERN = Pattern.compile(format("^%s\\s+(\\S*)$", TOKEN_PREFIX));
    static final String[] ALLOW_HEADERS = {
            "Access-Control-Expose-Headers",
            "Authorization",
            "accept",
            "access-control-request-headers",
            "access-control-request-method",
            "content-type",
            "observe",
            "origin",
            "responseType",
            "x-requested-with"
    };

    @NonNull JwtUserDetailsService jwtUserDetailsService;
    @NonNull JwtTokenUtil jwtTokenUtil;

    private void authenticate(HttpServletRequest request) {
        final String tokenHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (tokenHeader == null)
            return;

        final String token = getTokenFromHeader(tokenHeader);
        if (token == null) {
            logger.warn("JWT Token does not begin with Bearer String");
            return;
        }

        try {
            setContext(request, token);
        } catch (IllegalArgumentException e) {
            log.error("Unable to get JWT Token", e);
        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            log.error("JWT Token has expired", e);
        }
    }

    private void setContext(final HttpServletRequest request, final String token) {
        final String username = jwtTokenUtil.getUsernameFromToken(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
    }

    private String getTokenFromHeader(String header) {
        Matcher matcher = TOKEN_PATTERN.matcher(header);
        return matcher.find() && matcher.groupCount() == 1 ? matcher.group(1) : null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            log.info("Pre-flight");
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
            response.setHeader("Access-Control-Allow-Headers", String.join(",", ALLOW_HEADERS));
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        authenticate(request);
        chain.doFilter(request, response);
    }
}
