package com.olegandreevich.tms.security;

import com.olegandreevich.tms.entities.enums.Role;
import com.olegandreevich.tms.servicies.UserDetailsServiceTMS;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/** * Фильтр для проверки и обработки JWT-токена в каждом запросе. */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** * Сервис для получения деталей пользователя. */
    @Autowired
    private UserDetailsServiceTMS userDetailsServiceTMS;

    /** * Провайдер для работы с JWT-токенами. */
    @Autowired
    private JwtTokenProvider tokenProvider;

    /** * Основной метод фильтрации запроса. *
     * @param request HTTP-запрос.
     * @param response HTTP-ответ.
     * @param filterChain цепочка фильтров.
     * @throws ServletException исключение сервлета.
     * @throws IOException исключение ввода-вывода. */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Получение JWT-токена из заголовка Authorization
        String jwt = resolveToken(request);

        // Проверяем валидность токена
        if (jwt != null && tokenProvider.isTokenValid(jwt)) {
            // Аутентификация пользователя
            authenticate(request, jwt);
        }

        // Продолжаем выполнение остальных фильтров
        filterChain.doFilter(request, response);
    }

    /** * Метод для аутентификации пользователя на основе JWT-токена. *
     * @param request HTTP-запрос.
     * @param jwt JWT-токен. */
    private void authenticate(HttpServletRequest request, String jwt) {
        // Извлечение имени пользователя из токена
        String username = tokenProvider.getUsernameFromJWT(jwt);

        // Извлечение роли из токена
        Role role = tokenProvider.getRoleFromJWT(jwt);

        // Создание списка полномочий
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role.name()));

        // Создание объекта UserDetails
        UserDetailsTMS userDetails = new UserDetailsTMS(username, "", authorities);

        // Создание объекта аутентификации
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Установка дополнительных данных аутентификации
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Сохранение аутентифицированного пользователя в контексте безопасности
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /** * Метод для извлечения JWT-токена из заголовка Authorization. *
     * @param req HTTP-запрос.
     * @return JWT-токен или null, если токен не найден. */
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}