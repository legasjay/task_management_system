package com.olegandreevich.tms.servicies;

import com.olegandreevich.tms.entities.Task;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.entities.enums.Role;
import com.olegandreevich.tms.repositories.UserRepository;
import com.olegandreevich.tms.security.UserDetailsTMS;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Сервис для проверки, кем является пользователь (администратор, исполнитель или автор задачи) */
@Service
@RequiredArgsConstructor
public class UserCheckService {

    private final UserRepository userRepository;
    private static final String CURRENT_USER_ID_CACHE_KEY = "current_user_id";
    private Long cachedUserId;

    /** * Инициализирует кэш текущего пользователя. */
    @PostConstruct
    public void init() {
        this.cachedUserId = null;
    }

    /** * Возвращает id текущего пользователя */
    @Cacheable(value = CURRENT_USER_ID_CACHE_KEY)
    Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetailsTMS) {
            String username = ((UserDetailsTMS) principal).getUsername();

            try {
                Optional<User> optionalUser = userRepository.findByEmail(username);

                if (optionalUser.isPresent()) {
                    return optionalUser.get().getId();
                } else {
                    throw new RuntimeException("Пользователь с таким email не найден.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при получении текущего ID пользователя.", e);
            }
        }

        throw new RuntimeException("Не удалось получить текущий ID пользователя.");
    }

    /** * Возвращает кэшированное значение ID текущего пользователя. *
     * @return ID текущего пользователя. */
    public Long getCachedUserId() {
        if (cachedUserId == null) {
            cachedUserId = getCurrentUserId();
        }
        return cachedUserId;
    }

    /** * Возвращает истину, если пользователь администратор */
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ADMIN.toString()));
    }

    /** * Возвращает истину, если пользователь исполнитель задачи */
    public boolean isAssignee(Long assigneeId) {
        return assigneeId.equals(getCurrentUserId());
    }

    /** * Возвращает истину, если пользователь автор задачи */
    public boolean isAuthor(Long authorId) {
        return authorId.equals(getCurrentUserId());
    }

}
