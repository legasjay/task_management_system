package com.olegandreevich.tms.servicies;

import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.repositories.UserRepository;
import com.olegandreevich.tms.util.EmailAlreadyExistsException;
import com.olegandreevich.tms.util.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        return userRepository.save(user);
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
}
