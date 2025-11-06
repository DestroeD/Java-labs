package org.example.lab2.service;

import org.example.lab2.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final Pattern EMAIL_RE =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_RE =
            Pattern.compile("^\\+?[0-9\\-()\\s]{7,}$");

    public RegisterResponse register(RegisterRequest req) {
        log.info("Register request: username='{}', email='{}'", req.getUsername(), req.getEmail());
        List<String> errors = new ArrayList<>();

        // Обов'язкові
        if (isBlank(req.getUsername())) errors.add("username: обов'язкове поле");
        if (isBlank(req.getPassword())) errors.add("password: обов'язкове поле");
        if (isBlank(req.getEmail()))    errors.add("email: обов'язкове поле");
        if (req.getBirthday() == null) errors.add("birthday: обов'язкове поле");

        // Формати
        if (!isBlank(req.getEmail()) && !EMAIL_RE.matcher(req.getEmail()).matches()) {
            errors.add("email: некоректний формат");
        }
        if (!isBlank(req.getPassword()) && req.getPassword().length() < 6) {
            errors.add("password: мінімум 6 символів");
        }
        if (req.getBirthday() != null && req.getBirthday().isAfter(LocalDate.now())) {
            errors.add("birthday: не може бути у майбутньому");
        }
        if (!isBlank(req.getPhoneNumber()) && !PHONE_RE.matcher(req.getPhoneNumber()).matches()) {
            errors.add("phoneNumber: некоректний формат");
        }

        if (!errors.isEmpty()) {
            log.warn("Register validation failed: {}", errors);
            throw new ValidationException(errors);
        }

        // Mock success
        String id = UUID.randomUUID().toString();
        RegisterResponse resp = new RegisterResponse(
                id,
                req.getUsername(),
                req.getEmail(),
                "Користувача успішно зареєстровано (mock)"
        );
        log.info("Register success for '{}', id={}", req.getUsername(), id);
        return resp;
    }

    public LoginResponse login(LoginRequest req) {
        log.info("Login request: username='{}'", req.getUsername());
        List<String> errors = new ArrayList<>();

        if (isBlank(req.getUsername())) errors.add("username: обов'язкове поле");
        if (isBlank(req.getPassword())) errors.add("password: обов'язкове поле");

        if (!errors.isEmpty()) {
            log.warn("Login validation failed: {}", errors);
            throw new ValidationException(errors);
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        LoginResponse resp = new LoginResponse(token, "Bearer", "Авторизація успішна (mock)");
        log.info("Login success for '{}'", req.getUsername());
        return resp;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
