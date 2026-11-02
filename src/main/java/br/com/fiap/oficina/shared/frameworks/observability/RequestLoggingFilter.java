package br.com.fiap.oficina.shared.frameworks.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
        var previous = MDC.get("request_id");
        var supplied = request.getHeader("X-Request-ID");
        var requestId = supplied != null && supplied.matches("[A-Za-z0-9._-]{1,64}")
                ? supplied : UUID.randomUUID().toString();
        MDC.put("request_id", requestId);
        response.setHeader("X-Request-ID", requestId);
        long start = System.nanoTime();
        Throwable failure = null;
        try {
            chain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException exception) {
            failure = exception;
            throw exception;
        } finally {
            try {
                var log = LoggerFactory.getLogger(RequestLoggingFilter.class);
                var route = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
                int status = failure == null ? response.getStatus() : 500;
                log.atInfo().addKeyValue("event", "http.request")
                        .addKeyValue("http_method", request.getMethod())
                        .addKeyValue("http_route", route == null ? "unmapped" : route.toString())
                        .addKeyValue("http_status", status)
                        .addKeyValue("duracao_ms", (System.nanoTime() - start) / 1_000_000.0)
                        .log("Requisicao concluida");
                if (status >= 500) {
                    log.atError().addKeyValue("event", "http.failed")
                            .addKeyValue("http_route", route == null ? "unmapped" : route.toString())
                            .addKeyValue("error_type", failure == null ? "HttpServerError" : failure.getClass().getName())
                            .log("Falha no processamento da requisicao");
                }
            } finally {
                if (previous == null) MDC.remove("request_id");
                else MDC.put("request_id", previous);
            }
        }
    }
}
