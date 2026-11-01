package com.weddinglage.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weddinglage.config.WeddinglageProperties;
import com.weddinglage.web.error.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class RsvpRateLimitFilter extends OncePerRequestFilter {

    private final WeddinglageProperties properties;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public RsvpRateLimitFilter(WeddinglageProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.startsWith("/api/public/rsvp") && ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod())));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String key = request.getRemoteAddr();
        long minute = Instant.now().getEpochSecond() / 60;
        Window window = windows.compute(key, (k, existing) -> {
            if (existing == null || existing.minute != minute) {
                return new Window(minute, new AtomicInteger(1));
            }
            existing.count.incrementAndGet();
            return existing;
        });
        if (window.count.get() > properties.getRsvpRateLimitPerMinute()) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), new ErrorResponse("RATE_LIMITED", "Demasiadas peticiones RSVP"));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private record Window(long minute, AtomicInteger count) {
    }
}
