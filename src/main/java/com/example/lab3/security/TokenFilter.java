package com.example.lab3.security;

import com.example.lab3.token.TokenPayload;
import com.example.lab3.token.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class TokenFilter {

    private static final Logger log = LoggerFactory.getLogger(TokenFilter.class);

    private final TokenService tokenService;

    public TokenFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Інтерцептор: викликає метод контролера лише якщо токен валідний
     * (якщо метод позначено @TokenRequired).
     */
    public void invoke(Object controller, String methodName, Request request) {
        try {
            Method method = controller.getClass().getMethod(methodName, Request.class);

            boolean tokenRequired = method.isAnnotationPresent(TokenRequired.class);

            if (tokenRequired) {
                log.info("Перевірка токена для методу {}", methodName);

                String token = extractToken(request);

                TokenPayload payload = tokenService.validate(token);
                if (payload == null) {
                    log.warn("Доступ заборонено: недійсний або відсутній токен");
                    return;
                }

                // додаємо метадані користувача в request
                request.setAttribute("tokenPayload", payload);
                log.info("Доступ дозволено користувачу '{}'", payload.getUsername());
            } else {
                log.info("Метод {} не вимагає токена", methodName);
            }

            // Викликаємо сам ендпойнт
            method.invoke(controller, request);

        } catch (NoSuchMethodException e) {
            log.error("Метод {}(Request) не знайдено у {}", methodName, controller.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Помилка при виклику ендпойнта {}: {}", methodName, e.getMessage());
        }
    }

    private String extractToken(Request request) {
        // умовно: шукаємо в заголовку X-Auth-Token
        return request.getHeader("X-Auth-Token");
    }
}
