package com.olegandreevich.tms.security;

import com.olegandreevich.tms.entities.enums.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    @Value("${app.jwtExpirationInMs}")
    private long jwtExpirationInMs;

    @Autowired
    public CustomAuthenticationSuccessHandler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        UserDetailsTMS userDetailsTMS = (UserDetailsTMS) authentication.getPrincipal();

        // Извлекаем роль из UserDetails
        Collection<? extends GrantedAuthority> authorities = userDetailsTMS.getAuthorities();
        List<Role> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(Role::valueOf)
                .collect(Collectors.toList());
        String jwt = tokenProvider.generateToken(username, roles.get(0));

        Cookie cookie = new Cookie("accessToken", jwt);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge((int) jwtExpirationInMs / 1000);

        response.addCookie(cookie);

        response.sendRedirect("/swagger-ui.html");
    }
}
