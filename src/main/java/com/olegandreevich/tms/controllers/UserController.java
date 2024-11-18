package com.olegandreevich.tms.controllers;

import com.olegandreevich.tms.dto.UserRegistrationDto;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.servicies.UserService;
import com.olegandreevich.tms.util.EmailAlreadyExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationDto dto) {
        if (userService.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User(dto.getEmail(), dto.getPassword());
        user.setRole(User.Role.USER); // Устанавливаем роль по умолчанию
        userService.register(user);

        return ResponseEntity.ok(user);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable Long userId) {
        return userService.findById(userId);
    }
}
