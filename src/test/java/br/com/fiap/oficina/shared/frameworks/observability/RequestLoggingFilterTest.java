package br.com.fiap.oficina.shared.frameworks.observability;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.jupiter.api.Assertions.*;

class RequestLoggingFilterTest {
    @Test
    void propagatesRequestIdAndRestoresThreadContext() throws Exception {
        var request = new MockHttpServletRequest("GET", "/ordens-servico");
        request.addHeader("X-Request-ID", "req-123");
        var response = new MockHttpServletResponse();
        MDC.put("request_id", "previous");
        try {
            new RequestLoggingFilter().doFilter(request, response, (req, res) ->
                    assertEquals("req-123", MDC.get("request_id")));
            assertEquals("req-123", response.getHeader("X-Request-ID"));
            assertEquals("previous", MDC.get("request_id"));
        } finally { MDC.clear(); }
    }

    @Test
    void replacesUntrustedIdAndCleansContextOnFailure() {
        var request = new MockHttpServletRequest("POST", "/ordens-servico");
        request.setServletPath("/ordens-servico");
        request.addHeader("X-Request-ID", "invalid id\nvalue");
        var response = new MockHttpServletResponse();
        var logger = (Logger) LoggerFactory.getLogger(RequestLoggingFilter.class);
        var appender = new ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);
        try {
            assertThrows(ServletException.class, () -> new RequestLoggingFilter().doFilter(
                    request, response, (req, res) -> {
                        throw new ServletException(new DataAccessResourceFailureException("sensitive SQL"));
                    }));
            assertDoesNotThrow(() -> java.util.UUID.fromString(response.getHeader("X-Request-ID")));
            assertNull(MDC.get("request_id"));
            var text = appender.list.stream().flatMap(e -> e.getKeyValuePairs().stream())
                    .map(Object::toString).toList().toString();
            assertTrue(text.contains("http.failed"));
            assertFalse(text.contains("os.processing_failed"));
            assertFalse(text.contains("integration.failed"));
            assertFalse(appender.list.toString().contains("sensitive SQL"));
        } finally { logger.detachAppender(appender); appender.stop(); }
    }

    @Test
    void doesNotAlertOnClientErrors() throws Exception {
        var request = new MockHttpServletRequest("POST", "/ordens-servico");
        var response = new MockHttpServletResponse();
        new RequestLoggingFilter().doFilter(request, response, (req, res) -> response.setStatus(422));
        assertEquals(422, response.getStatus());
        assertNull(MDC.get("request_id"));
    }
}
