package com.olegandreevich.tms.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.olegandreevich.tms.entities.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

/** * Компонент для генерации и валидации JWT-токенов. */
@Component
public class JwtTokenProvider {

    /** * Секретный ключ для подписи JWT-токенов. */
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    /** * Время жизни JWT-токена в миллисекундах. */
    @Value("${app.jwtExpirationInMs}")
    private long jwtExpirationInMs;

    /** * Генерирует JWT-токен для указанного пользователя и роли. *
     * @param username Имя пользователя.
     * @param role Роль пользователя.
     * @return Сгенерированный JWT-токен.
     * @throws IllegalStateException Если произошла ошибка при создании токена. */
    public String generateToken(String username, Role role) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            return JWT.create()
                    .withIssuer("tms")             // Указывает издателя токена
                    .withSubject(username)         // Указывает субъект (пользователь)
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationInMs)) // Устанавливает срок действия токена
                    .withClaim("role", role.name()) // Добавляет роль в токен
                    .sign(algorithm);              // Подписывает токен
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Не удалось создать JWT-токен.", e);
        }
    }

    /** * Извлекает имя пользователя из JWT-токена. *
     * @param token JWT-токен.
     * @return Имя пользователя.
     * @throws IllegalStateException Если токен недействителен или истек. */
    public String getUsernameFromJWT(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            return JWT.require(algorithm)
                    .withIssuer("tms")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("JWT токен недействителен или истек.", e);
        }
    }

    /** * Извлекает роль пользователя из JWT-токена. *
     * @param token JWT-токен.
     * @return Роль пользователя.
     * @throws IllegalStateException Если токен недействителен или истек. */
    public Role getRoleFromJWT(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            String roleString = JWT.require(algorithm)
                    .withIssuer("tms")
                    .build()
                    .verify(token)
                    .getClaim("role").asString(); // Получает роль из токена
            return Role.valueOf(roleString);                     // Преобразует строку в перечисление Role
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("JWT токен недействителен или истек.", e);
        }
    }

    /** * Проверяет валидность JWT-токена. *
     * @param token JWT-токен.
     * @return {@code true}, если токен действительный, иначе {@code false}. */
    public boolean isTokenValid(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            JWT.require(algorithm)
                    .withIssuer("tms")
                    .build()
                    .verify(token);
            return true;
        } catch (IllegalArgumentException | JWTVerificationException e) {
            return false;
        }
    }
}