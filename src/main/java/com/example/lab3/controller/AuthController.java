package com.example.lab3.controller;

import com.example.lab3.security.Request;
import com.example.lab3.security.TokenRequired;
import com.example.lab3.token.TokenPayload;
import com.example.lab3.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final TokenService tokenService;

    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void register(Request request) {
        String username = request.getBody();
        String token = tokenService.generateToken(username);
        log.info("Користувач '{}' зареєстрований. Токен: {}", username, token);
        System.out.println("REGISTER: Твій токен: " + token);
    }

    public void login(Request request) {
        String username = request.getBody();
        String token = tokenService.generateToken(username);
        log.info("Користувач '{}' увійшов. Токен: {}", username, token);
        System.out.println("LOGIN: Твій токен: " + token);
    }

    @TokenRequired
    public void refreshToken(Request request) {
        String oldToken = request.getHeader("X-Auth-Token");
        String newToken = tokenService.refresh(oldToken);
        if (newToken == null) {
            System.out.println("REFRESH: Не вдалося оновити токен");
        } else {
            System.out.println("REFRESH: Новий токен: " + newToken);
        }
    }

    @TokenRequired
    public void invalidateToken(Request request) {
        String token = request.getHeader("X-Auth-Token");
        tokenService.invalidate(token);
        System.out.println("INVALIDATE: Токен інвалідовано");
    }

    @TokenRequired
    public void getProfile(Request request) {
        TokenPayload payload = (TokenPayload) request.getAttribute("tokenPayload");
        System.out.println("PROFILE: Привіт, " + payload.getUsername() +
                "! Це твій профіль в " + payload.getAppName());
    }
}
