/**
 * DANA Indonesia
 * Copyright (c) 2008‐2025 All Rights Reserved.
 */
package org.enkrip.atkins.shared.websocket;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Dian Aditya
 * @version $Id: WebSocketSessionService.java, v 0.1 2025‐09‐16 17.43 Dian Aditya Exp $$
 */
@Service
public class WebSocketSessionService {
    private final Map<String, String> usernameSessionId = new ConcurrentHashMap<>();
    private final Map<String, String> sessionIdUsername = new ConcurrentHashMap<>();
    private final Map<String, Set<WebSocketSession>> httpSessionMap = new ConcurrentHashMap<>();

    public String afterConnectionEstablished(WebSocketSession session) throws IOException {
        String username = session.getHandshakeHeaders()
                .getOrDefault("X-Username", List.of(UUID.randomUUID().toString()))
                .getFirst();

        String httpSessionId = getHttpSessionId(session);

        httpSessionMap.computeIfAbsent(httpSessionId, s -> new HashSet<>()).add(session);
        usernameSessionId.put(username, httpSessionId);
        sessionIdUsername.put(httpSessionId, username);

        return username;
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = getHttpSessionId(session);
        Set<WebSocketSession> webSocketSessions = httpSessionMap.get(sessionId);

        Optional.ofNullable(webSocketSessions).ifPresent(s -> s.remove(session));
        String username = sessionIdUsername.remove(sessionId);
        Optional.ofNullable(username).ifPresent(usernameSessionId::remove);
    }

    private String getHttpSessionId(WebSocketSession session) {
        String httpSessionId = (String) session.getAttributes().get(HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME);

        if (StringUtils.isBlank(httpSessionId)) {
            throw new RuntimeException("HTTP session ID is missing in WebSocket session attributes");
        }

        return httpSessionId;
    }

    public int sendMessage(String username, WebSocketMessage<?> message) {
        String sessionId = usernameSessionId.get(username);
        Set<WebSocketSession> webSocketSessions = httpSessionMap.get(sessionId);

        AtomicInteger atomicInteger = new AtomicInteger();
        webSocketSessions.forEach(webSocketSession -> {
            try {
                webSocketSession.sendMessage(message);
                atomicInteger.incrementAndGet();
            } catch (IOException e) {
                // throw new RuntimeException(e);
            }
        });
        return atomicInteger.get();
    }

    public Set<String> getAllActiveUsernames() {
        return new TreeSet<>(usernameSessionId.keySet());
    }

    public Set<String> getAllActiveUsernames(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        return sessionIdUsername.entrySet().stream()
                .filter(e -> !e.getKey().equals(sessionId))
                .map(Map.Entry::getValue)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public String getCurrentUsername(WebSocketSession session) {
        return sessionIdUsername.get(getHttpSessionId(session));
    }

    public String getCurrentUsername(HttpServletRequest request) {
        return sessionIdUsername.get(request.getSession().getId());
    }
}
