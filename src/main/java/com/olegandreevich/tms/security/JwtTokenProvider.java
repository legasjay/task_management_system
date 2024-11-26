package com.olegandreevich.tms.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.olegandreevich.tms.entities.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private long jwtExpirationInMs;

    public String generateToken(String username, Role role) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            return JWT.create()
                    .withIssuer("tms")
                    .withSubject(username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                    .withClaim("role", role.name())
                    .sign(algorithm);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Не удалось создать JWT-токен.", e);
        }
    }

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

    public Role getRoleFromJWT(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            String roleString = JWT.require(algorithm)
                    .withIssuer("tms")
                    .build()
                    .verify(token)
                    .getClaim("role").asString(); // Получает роль из токена
            return Role.valueOf(roleString); // Преобразует строку в перечисление Role
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("JWT токен недействителен или истек.", e);
        }
    }

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