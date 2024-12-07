package com.olegandreevich.tms.controllers;

import com.olegandreevich.tms.dto.UserRegistrationDto;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.servicies.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "Операции с пользователями")
@Validated
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    /** * Конструктор класса UserController. *
     * @param passwordEncoder Кодировщик паролей.
     * @param userService Сервис для работы с пользователями. */
    public UserController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    /** * Регистрация нового пользователя. *
     * @param dto Данные для регистрации пользователя.
     * @return Зарегистрированный пользователь. */
    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя",
            description = "Регистрирует нового пользователя на основе предоставленных данных.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные пользователя")
            })
    public ResponseEntity<User> register(
            @Parameter(description = "Данные для регистрации пользователя") @Valid @RequestBody UserRegistrationDto dto
    ) {
        User user = userService.register(dto);
        return ResponseEntity.ok(user);
    }

    /** * Получение списка всех пользователей. *
     * @return Список всех пользователей. */
    @GetMapping
    @Operation(summary = "Получение списка всех пользователей",
            description = "Возвращает список всех зарегистрированных пользователей.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен")
            })
    public List<User> findAll() {
        return userService.findAll();
    }

    /** * Получение пользователя по идентификатору. *
     * @param userId Идентификатор пользователя.
     * @return Пользователь с указанным идентификатором. */
    @GetMapping("/{userId}")
    @Operation(summary = "Получение пользователя по идентификатору",
            description = "Возвращает пользователя с указанным идентификатором.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно получен"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            })
    public User findById(
            @Parameter(description = "Идентификатор пользователя") @PathVariable Long userId
    ) {
        return userService.findById(userId);
    }
}
