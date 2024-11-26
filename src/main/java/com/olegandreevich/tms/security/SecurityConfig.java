package com.olegandreevich.tms.security;

import com.olegandreevich.tms.servicies.UserDetailsServiceTMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private UserDetailsServiceTMS userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users/register").permitAll()
                        // Регистрация доступна всем
                        .requestMatchers(HttpMethod.GET,"/api/users").hasRole("ADMIN")
                        // Просмотр всех пользователей доступен только админу
                        .requestMatchers(HttpMethod.GET,"/api/users/{userId}").hasRole("ADMIN")
                        // Просмотр отдельного пользователя доступен только админу
                        .requestMatchers(HttpMethod.GET, "/api/tasks").hasAnyRole("USER", "ADMIN")
                        // Получение списка задач доступно пользователям и админам
                        .requestMatchers(HttpMethod.POST, "/api/tasks").hasAnyRole("USER", "ADMIN")
                        // Создание новой задачи доступно пользователям и админам
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/{id}").hasAnyRole("USER", "ADMIN")
                        // Редактирование задачи доступно пользователям и админам
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/{id}").hasRole("ADMIN")
                        // Удаление задачи доступно только админу
                        .requestMatchers("/api/tasks/{taskId}/comments").hasAnyRole("USER", "ADMIN")
                        // Комментарии доступны пользователям и админам
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").authenticated()
                        // Разрешить доступ к Swagger UI только аутентифицированным пользователям
                        .anyRequest().authenticated() // Остальные запросы запрещены
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form
                                .defaultSuccessUrl("/swagger-ui.html")
                                .successHandler(customAuthenticationSuccessHandler)
                                                    // Указываем наш кастомный обработчик успеха
                                .failureUrl("/login-error")
                                .permitAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtCookieAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}