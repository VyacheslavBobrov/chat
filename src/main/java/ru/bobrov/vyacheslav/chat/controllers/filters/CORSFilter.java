package ru.bobrov.vyacheslav.chat.controllers.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter implements Filter {
    static final String[] ALLOW_HEADERS = {
            "x-requested-with",
            "Content-Type",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "Authorization",
            "Access-Control-Allow-Origin",
            "Access-Control-Expose-Headers",
            "accept",
            "observe",
            "origin",
            "responseType",
            "x-requested-with",
    };

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");

        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            log.info("Pre-flight: " + origin);
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin");
            response.setHeader("Access-Control-Allow-Headers", String.join(",", ALLOW_HEADERS));
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        log.info("CORS: " + origin);
        response.setHeader("Access-Control-Allow-Origin", origin); //"http://127.0.0.1:8080");//"http://localhost:8081");
        response.setHeader("Vary", "Origin");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", String.join(",", ALLOW_HEADERS));
        response.setHeader("Access-Control-Allow-Credentials", "true");

        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

}
