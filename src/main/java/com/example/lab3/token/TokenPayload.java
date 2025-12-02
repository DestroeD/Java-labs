package com.example.lab3.token;

import java.time.Instant;

public class TokenPayload {
    private final String username;
    private final String appName;
    private final Instant issuedAt;
    private final Instant expiresAt;

    public TokenPayload(String username, String appName, Instant issuedAt, Instant expiresAt) {
        this.username = username;
        this.appName = appName;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public String getUsername() {
        return username;
    }

    public String getAppName() {
        return appName;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
