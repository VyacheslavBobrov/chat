package ru.bobrov.vyacheslav.chat.controllers.filters;

import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_MAX_AGE;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.http.HttpHeaders.VARY;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter implements Filter {
    static final String[] ALLOWED_HEADERS = {
            CONTENT_TYPE,
            HttpHeaders.X_REQUESTED_WITH,
            ACCESS_CONTROL_REQUEST_METHOD,
            ACCESS_CONTROL_REQUEST_HEADERS,
            AUTHORIZATION,
            ACCESS_CONTROL_ALLOW_ORIGIN,
            ACCESS_CONTROL_EXPOSE_HEADERS,
            ACCEPT,
            "observe",
            ORIGIN,
            "responseType",
    };

    static final List<String> ALLOWED_METHODS = Stream.of(
            POST, GET, OPTIONS, PUT, DELETE
    ).map(HttpMethod::name).collect(Collectors.toUnmodifiableList());

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        val response = (HttpServletResponse) res;
        val request = (HttpServletRequest) req;

        val allowedOrigin = request.getHeader(ORIGIN); //TODO: на время разработки, в проде заменить на адрес фронта

        if (request.getMethod().equalsIgnoreCase(OPTIONS.name())) {
            log.info("Pre-flight: " + allowedOrigin);
            response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin);
            response.setHeader(VARY, ORIGIN);
            response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, String.join(",", ALLOWED_HEADERS));
            response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.TRUE.toString());
            response.setStatus(SC_OK);
            return;
        }

        log.info("CORS: " + allowedOrigin);
        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin);
        response.setHeader(VARY, ORIGIN);
        response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, String.join(",", ALLOWED_METHODS));
        response.setHeader(ACCESS_CONTROL_MAX_AGE, "3600");
        response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, String.join(",", ALLOWED_HEADERS));
        response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.TRUE.toString());

        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

}
