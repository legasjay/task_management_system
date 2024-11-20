package com.olegandreevich.tms.controllers;

import com.olegandreevich.tms.dto.UserRegistrationDto;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.entities.enums.Role;
import com.olegandreevich.tms.servicies.UserService;
import com.olegandreevich.tms.util.EmailAlreadyExistsException;
import com.olegandreevich.tms.util.InvalidPasswordException;
import com.olegandreevich.tms.util.UsernameAlreadyExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationDto dto) {
        User user = userService.register(dto);
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
