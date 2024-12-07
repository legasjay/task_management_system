package com.olegandreevich.tms.security;

import com.olegandreevich.tms.entities.enums.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** * Класс обработчика успешного входа пользователя. */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /** * Путь к файлу свойств, где хранится время жизни JWT-токена. */
    @Value("${app.jwtExpirationInMs}")
    private long jwtExpirationInMs;

    /** * Провайдер для генерации JWT-токенов. */
    private final JwtTokenProvider tokenProvider;

    /** * Конструктор класса. * * @param tokenProvider провайдер для генерации JWT-токенов. */
    @Autowired
    public CustomAuthenticationSuccessHandler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /** * Метод обработки успешной аутентификации. *
     * @param request HTTP-запрос. * @param response HTTP-ответ.
     * @param authentication объект аутентификации.
     * @throws IOException исключение ввода-вывода.
     * @throws ServletException исключение сервлета. */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Получение имени пользователя
        String username = authentication.getName();

        // Преобразование объекта Principal в тип UserDetailsTMS
        UserDetailsTMS userDetailsTMS = (UserDetailsTMS) authentication.getPrincipal();

        // Извлечение ролей из UserDetails
        Collection<? extends GrantedAuthority> authorities = userDetailsTMS.getAuthorities();
        List<Role> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(Role::valueOf)
                .collect(Collectors.toList());

        // Генерация JWT-токена
        String jwt = tokenProvider.generateToken(username, roles.get(0));

        // Создание куки с токеном
        Cookie cookie = new Cookie("accessToken", jwt);
        cookie.setPath("/");         // Устанавливаем путь для всех запросов
        cookie.setHttpOnly(true);    // Куки доступны только через HTTP
        cookie.setSecure(true);      // Передача по защищенному соединению
        cookie.setMaxAge((int) jwtExpirationInMs / 1000); // Время жизни куки

        // Добавление куки в ответ
        response.addCookie(cookie);

        // Перенаправляем пользователя после успешной аутентификации
        response.sendRedirect("/swagger-ui.html");
    }
}
