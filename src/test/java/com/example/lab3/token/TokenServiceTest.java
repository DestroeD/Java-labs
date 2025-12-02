package com.example.lab3.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenServiceTest {

    @Test
    void generateToken_containsCorrectData() {
        TokenService service = new TokenService();

        String username = "testUser";
        String token = service.generateToken(username);
        assertNotNull(token, "Токен не повинен бути null");

        TokenPayload payload = service.parse(token);
        assertNotNull(payload, "payload не повинен бути null");

        assertEquals(username, payload.getUsername());
        assertEquals("Lab3TokenApp", payload.getAppName());
        assertTrue(payload.getExpiresAt().isAfter(payload.getIssuedAt()));
    }

    @Test
    void validate_returnsNullForInvalidToken() {
        TokenService service = new TokenService();

        TokenPayload payload = service.validate("якийсь_сміттєвий_токен");
        assertNull(payload, "Для неправильного токена має бути null");
    }
}
