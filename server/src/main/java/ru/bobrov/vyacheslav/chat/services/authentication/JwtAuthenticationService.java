package ru.bobrov.vyacheslav.chat.services.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.services.utils.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static ru.bobrov.vyacheslav.chat.services.Constants.TOKEN_PREFIX;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@NonNull
@Slf4j
public class JwtAuthenticationService {
    static final Pattern TOKEN_PATTERN = Pattern.compile(format("^%s\\s+(\\S*)$", TOKEN_PREFIX));

    JwtUserDetailsService jwtUserDetailsService;
    JwtTokenUtil jwtTokenUtil;

    public void authenticate(HttpServletRequest request) {
        final String tokenHeader = request.getHeader(AUTHORIZATION);
        try {
            if (isNull(tokenHeader)) {
                log.error("Token header not find in request");
                return;
            }


            final String token = extractTokenFromHeader(tokenHeader);
            setContext(request, token);
        } catch (IllegalArgumentException e) {
            log.error("Unable to set security context", e);
        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            log.error("JWT Token has expired", e);
        }
    }

    public Principal createPrincipalFromToken(String token) {
        final String username = jwtTokenUtil.getUsernameFromToken(token);
        if (isNull(username))
            throw new IllegalArgumentException(format("User for token: %s not found!", token));

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
        if (!jwtTokenUtil.validateToken(token, userDetails))
            throw new IllegalArgumentException("Invalid token: " + token);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    private void setContext(final HttpServletRequest request, final String token) {
        final UsernamePasswordAuthenticationToken authenticationToken
                = (UsernamePasswordAuthenticationToken) createPrincipalFromToken(token);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        log.info(format("User: %s - authenticated, token: %s", authenticationToken.getName(), token));
    }

    public String extractTokenFromHeader(String header) {
        Matcher matcher = TOKEN_PATTERN.matcher(header);
        String token = matcher.find() && matcher.groupCount() == 1 ? matcher.group(1) : null;
        if (isNull(token)) {
            throw new IllegalArgumentException("Illegal JWT Token header: " + header);
        }
        return token;
    }
}
