package com.olegandreevich.tms.servicies;

import com.olegandreevich.tms.dto.UserRegistrationDto;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.entities.enums.Role;
import com.olegandreevich.tms.repositories.UserRepository;
import com.olegandreevich.tms.util.EmailAlreadyExistsException;
import com.olegandreevich.tms.util.InvalidPasswordException;
import com.olegandreevich.tms.util.ResourceNotFoundException;
import com.olegandreevich.tms.util.UsernameAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(UserRegistrationDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(dto.getUsername());
        }

        if (!isValidPassword(dto.getPassword())) {
            throw new InvalidPasswordException("Пароль слишком короткий. Должен быть минимум 8 символов.");
        }

        String encryptedPassword = passwordEncoder.encode(dto.getPassword());
        User user = new User(dto.getEmail(), dto.getUsername(), encryptedPassword);
        user.setRole(Role.USER); // Устанавливаем роль по умолчанию
        return userRepository.save(user);
    }

    // Метод для проверки валидности пароля
    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public void makeAdmin(Long userId) {
        User user = findById(userId);
        user.promoteToAdmin();
        userRepository.save(user);
    }

    public void revokeAdmin(Long userId) {
        User user = findById(userId);
        user.demoteToUser();
        userRepository.save(user);
    }

    public void updatePassword(String username, String plainTextPassword) {
        String encodedPassword = passwordEncoder.encode(plainTextPassword);
        userRepository.updatePassword(encodedPassword, username);
    }
}
