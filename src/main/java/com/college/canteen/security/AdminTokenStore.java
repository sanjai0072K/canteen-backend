package com.college.canteen.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Keeps the login tokens of admins in memory (token -> expiry time).
// Restarting the app logs everybody out, admin just logs in again.
@Component
public class AdminTokenStore {

    private static final Duration VALIDITY = Duration.ofHours(8);

    private final Map<String, Instant> tokens = new ConcurrentHashMap<>();

    public String issue() {
        String token = UUID.randomUUID().toString();
        tokens.put(token, Instant.now().plus(VALIDITY));
        return token;
    }

    public boolean isValid(String token) {
        Instant expiry = tokens.get(token);
        if (expiry == null) {
            return false;
        }
        if (expiry.isBefore(Instant.now())) {
            tokens.remove(token);
            return false;
        }
        return true;
    }

    public void revoke(String token) {
        if (token != null) {
            tokens.remove(token);
        }
    }

    // "Authorization: Bearer abc123" -> "abc123"
    public static String extract(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7).trim();
        }
        return null;
    }
}
