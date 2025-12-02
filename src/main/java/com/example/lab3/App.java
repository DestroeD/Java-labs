package com.example.lab3;

import com.example.lab3.controller.AuthController;
import com.example.lab3.security.Request;
import com.example.lab3.security.TokenFilter;
import com.example.lab3.token.TokenService;

public class App {
    public static void main(String[] args) {

        TokenService tokenService = new TokenService();
        AuthController authController = new AuthController(tokenService);
        TokenFilter filter = new TokenFilter(tokenService);

        // 1) Реєстрація
        Request registerRequest = new Request();
        registerRequest.setBody("maks");
        authController.register(registerRequest);

        // 2) Уявімо, що логін повернув токен
        String token = tokenService.generateToken("maks");

        // 3) Викликаємо захищений ендпойнт /profile
        Request profileRequest = new Request();
        profileRequest.setHeader("X-Auth-Token", token);

        filter.invoke(authController, "getProfile", profileRequest);

        // 4) Інвалідація токена
        Request invReq = new Request();
        invReq.setHeader("X-Auth-Token", token);
        filter.invoke(authController, "invalidateToken", invReq);

        // 5) Повторний доступ із тим самим токеном → має заблокувати
        Request profileRequest2 = new Request();
        profileRequest2.setHeader("X-Auth-Token", token);
        filter.invoke(authController, "getProfile", profileRequest2);
    }
}
