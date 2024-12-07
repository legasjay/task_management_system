package com.olegandreevich.tms.servicies;

import com.olegandreevich.tms.dto.UserRegistrationDto;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.entities.enums.Role;
import com.olegandreevich.tms.repositories.UserRepository;
import com.olegandreevich.tms.util.exceptions.EmailAlreadyExistsException;
import com.olegandreevich.tms.util.exceptions.InvalidPasswordException;
import com.olegandreevich.tms.util.exceptions.ResourceNotFoundException;
import com.olegandreevich.tms.util.exceptions.UsernameAlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** * Сервис для работы с пользователями. * Включает регистрацию новых пользователей, изменение ролей
 * и обновление паролей. */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** * Регистрирует нового пользователя. * * @param dto Объект регистрации пользователя.
     * @return Зарегистрированный пользователь.
     * @throws EmailAlreadyExistsException если электронная почта уже занята.
     * @throws UsernameAlreadyExistsException если имя пользователя уже занято.
     * @throws InvalidPasswordException если пароль не соответствует требованиям безопасности. */
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

    /** * Проверяет валидность пароля. *
     * @param password Пароль для проверки.
     * @return true, если пароль удовлетворяет минимальным требованиям (длина не менее 8 символов), иначе false. */
    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    /** * Возвращает список всех пользователей. * * @return Список всех пользователей. */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /** * Находит пользователя по электронной почте. * * @param email Электронная почта пользователя.
     * @return Найденный пользователь. */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /** * Находит пользователя по ID. * * @param userId ID пользователя. * @return Найденный пользователь.
     * @throws ResourceNotFoundException если пользователь с указанным ID не найден. */
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    /** * Повышает пользователя до роли администратора. *
     * * @param userId ID пользователя, которого нужно повысить до администратора.
     * * @throws ResourceNotFoundException если пользователь с указанным ID не найден. */
    public void makeAdmin(Long userId) {
        User user = findById(userId);
        user.promoteToAdmin();
        userRepository.save(user);
    }

    /** * Понижает права пользователя до обычной роли. *
     * @param userId ID пользователя, чьи права нужно понизить.
     * @throws ResourceNotFoundException если пользователь с указанным ID не найден. */
    public void revokeAdmin(Long userId) {
        User user = findById(userId);
        user.demoteToUser();
        userRepository.save(user);
    }

    /** * Обновляет пароль пользователя. *
     * @param username Имя пользователя, чей пароль нужно обновить.
     * @param plainTextPassword Новый пароль в виде обычного текста. */
    public void updatePassword(String username, String plainTextPassword) {
        String encodedPassword = passwordEncoder.encode(plainTextPassword);
        userRepository.updatePassword(encodedPassword, username);
    }
}
