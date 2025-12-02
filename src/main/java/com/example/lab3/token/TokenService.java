package com.example.lab3.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    private static final String APP_NAME = "Lab3TokenApp";
    private static final Duration TOKEN_TTL = Duration.ofMinutes(30);

    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    public String generateToken(String username) {
        Instant now = Instant.now();
        Instant expires = now.plus(TOKEN_TTL);

        String raw = username + "|" + APP_NAME + "|" +
                now.toEpochMilli() + "|" + expires.toEpochMilli();

        String token = Base64.getUrlEncoder()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));

        log.info("Згенеровано токен для користувача '{}', дійсний до {}", username, expires);

        return token;
    }

    public TokenPayload parse(String token) {
        try {
            String decoded = new String(
                    Base64.getUrlDecoder().decode(token),
                    StandardCharsets.UTF_8
            );
            String[] parts = decoded.split("\\|");
            if (parts.length != 4) {
                log.warn("Невірний формат токена");
                return null;
            }

            String username = parts[0];
            String appName = parts[1];
            Instant issuedAt = Instant.ofEpochMilli(Long.parseLong(parts[2]));
            Instant expiresAt = Instant.ofEpochMilli(Long.parseLong(parts[3]));

            return new TokenPayload(username, appName, issuedAt, expiresAt);
        } catch (Exception e) {
            log.warn("Помилка розпарсування токена: {}", e.getMessage());
            return null;
        }
    }

    public TokenPayload validate(String token) {
        if (token == null || token.isBlank()) {
            log.warn("Токен відсутній");
            return null;
        }

        if (invalidatedTokens.contains(token)) {
            log.warn("Токен вже інвалідовано");
            return null;
        }

        TokenPayload payload = parse(token);
        if (payload == null) {
            return null;
        }

        if (!APP_NAME.equals(payload.getAppName())) {
            log.warn("Невірна назва застосунку в токені");
            return null;
        }

        if (payload.isExpired()) {
            log.warn("Токен протермінований");
            return null;
        }

        log.info("Токен для користувача '{}' успішно валідовано", payload.getUsername());
        return payload;
    }

    public void invalidate(String token) {
        if (token != null) {
            invalidatedTokens.add(token);
            log.info("Токен інвалідовано");
        }
    }

    public String refresh(String oldToken) {
        TokenPayload payload = validate(oldToken);
        if (payload == null) {
            log.warn("Неможливо оновити токен: старий токен некоректний");
            return null;
        }
        invalidate(oldToken);
        String newToken = generateToken(payload.getUsername());
        log.info("Токен оновлено для користувача '{}'", payload.getUsername());
        return newToken;
    }
}
