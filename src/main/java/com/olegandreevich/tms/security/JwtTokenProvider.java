package com.olegandreevich.tms.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private long jwtExpirationInMs;

    public String generateToken(String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            return JWT.create()
                    .withIssuer("Your App Name")
                    .withSubject(username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                    .sign(algorithm);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Не удалось создать JWT-токен.", e);
        }
    }

    public String getUsernameFromJWT(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            return JWT.require(algorithm)
                    .withIssuer("Your App Name")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Не удалось получить имя пользователя из JWT-токена.", e);
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            JWT.require(algorithm)
                    .withIssuer("Your App Name")
                    .build()
                    .verify(token);
            return true;
        } catch (IllegalArgumentException | JWTVerificationException e) {
            return false;
        }
    }
}