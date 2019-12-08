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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.services.Constants.AUTHORIZATION_HEADER;
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
        authenticate(request, request.getHeader(AUTHORIZATION_HEADER));
    }

    public void authenticate(@NonNull String tokenHeader) {
        authenticate(null, tokenHeader);
    }

    private void authenticate(HttpServletRequest request, String tokenHeader) {
        if (tokenHeader == null)
            return;

        final String token = getTokenFromHeader(tokenHeader);
        if (token == null) {
            log.warn("JWT Token does not begin with Bearer String");
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
                if (request != null)
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info(format("User: %s - login to system, token: %s", userDetails.getUsername(), token));
            }
        }
    }

    private String getTokenFromHeader(String header) {
        Matcher matcher = TOKEN_PATTERN.matcher(header);
        return matcher.find() && matcher.groupCount() == 1 ? matcher.group(1) : null;
    }
}
