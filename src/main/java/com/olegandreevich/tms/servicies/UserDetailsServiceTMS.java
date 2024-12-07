package com.olegandreevich.tms.servicies;

import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.repositories.UserRepository;
import com.olegandreevich.tms.security.UserDetailsTMS;
import com.olegandreevich.tms.util.exceptions.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** * Реализация сервиса UserDetailsService для загрузки пользователя. */
@Service
public class UserDetailsServiceTMS implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /** * Загружает детали пользователя по электронной почте. * * @param email Электронная почта пользователя.
     * @return Детали пользователя. * @throws UsernameNotFoundException если пользователь
     * с указанной электронной почтой не найден. */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.map(UserDetailsTMS::build).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
