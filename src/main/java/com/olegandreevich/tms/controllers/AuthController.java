package com.olegandreevich.tms.controllers;

import com.olegandreevich.tms.entities.enums.Role;
import com.olegandreevich.tms.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Операции для получения токенов доступа")
@Validated
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/token")
    @Operation(summary = "Генерация токена доступа",
            description = "Генерирует токен доступа для авторизованного пользователя.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Токен успешно сгенерирован"),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации")
            })
    public ResponseEntity<Map<String, String>> generateToken(
            @Parameter(description = "Запрос на логин с данными пользователя")
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("Неверная почта или пароль.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(loginRequest.email(), loginRequest.role());
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", jwt);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public record LoginRequest(String email, String password, Role role) {
        public String email() {
            return email;
        }

        public String password() {
            return password;
        }

        public Role role() {
            return role;
        }
    }
}
