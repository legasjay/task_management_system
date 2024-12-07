package com.olegandreevich.tms.security;

import com.olegandreevich.tms.servicies.UserDetailsServiceTMS;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** * Фильтр для проверки и обработки JWT-токена, полученного из куки. */
@Component
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    /** * Сервис для работы с JWT-токенами. */
    @Autowired
    private JwtTokenProvider tokenProvider;

    /** * Сервис для получения деталей пользователя. */
    @Autowired
    private UserDetailsServiceTMS userDetailsServiceTMS;

    /** * Основной метод фильтрации запроса. *
     * @param request HTTP-запрос.
     * @param response HTTP-ответ.
     * @param filterChain цепочка фильтров.
     * @throws ServletException исключение сервлета.
     * @throws IOException исключение ввода-вывода. */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Извлечение JWT-токена из куки
        String jwt = extractJwtFromCookie(request);

        // Проверяем валидность токена
        if (jwt != null && tokenProvider.isTokenValid(jwt)) {
            // Аутентификация пользователя с помощью JWT-токена
            authenticateWithJwt(jwt, request);
        }

        // Продолжаем выполнение остальных фильтров
        filterChain.doFilter(request, response);
    }

    /** * Метод для извлечения JWT-токена из куки. *
     * @param request HTTP-запрос.
     * @return JWT-токен или null, если токен не найден. */
    private String extractJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /** * Метод для аутентификации пользователя с использованием JWT-токена. *
     * @param jwt JWT-токен.
     * @param request HTTP-запрос. */
    private void authenticateWithJwt(String jwt, HttpServletRequest request) {
        // Извлечение имени пользователя из токена
        String username = tokenProvider.getUsernameFromJWT(jwt);

        // Загрузка деталей пользователя
        UserDetails userDetails = userDetailsServiceTMS.loadUserByUsername(username);

        // Создание объекта аутентификации
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // Установка дополнительных данных аутентификации
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Сохранение аутентифицированного пользователя в контексте безопасности
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
