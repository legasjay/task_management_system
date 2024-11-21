package com.olegandreevich.tms.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    @Value("${app.jwtExpirationInMs}")
    private long jwtExpirationInMs;

    @Autowired
    public CustomAuthenticationSuccessHandler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName(); // Получаем имя пользователя из контекста аутентификации
        String jwt = tokenProvider.generateToken(username); // Генерируем JWT-токен

        Cookie cookie = new Cookie("accessToken", jwt);
        cookie.setPath("/"); // Доступен для всего домена
        cookie.setSecure(true); // Только через HTTPS
        cookie.setMaxAge((int) jwtExpirationInMs / 1000); // Срок действия токена
        response.addCookie(cookie);

        // Перенаправляем на Swagger UI
        response.sendRedirect("/swagger-ui.html");
    }
}
