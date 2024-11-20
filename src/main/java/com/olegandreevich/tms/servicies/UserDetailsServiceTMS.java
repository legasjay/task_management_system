package com.olegandreevich.tms.servicies;

import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.repositories.UserRepository;
import com.olegandreevich.tms.security.UserDetailsTMS;
import com.olegandreevich.tms.util.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceTMS implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.map(UserDetailsTMS::build).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
