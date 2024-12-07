package com.olegandreevich.tms.security;

import com.olegandreevich.tms.servicies.UserDetailsServiceTMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/** * Конфигурационный класс для настройки безопасности приложения. */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** * Фильтр для проверки и обработки JWT-токена в каждом запросе. */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /** * Фильтр для проверки и обработки JWT-токена, полученного из куки, в каждом запросе. */
    @Autowired
    private JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter;

    /** * Обработчик успешного завершения аутентификации. */
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    /** * Сервис для получения деталей пользователя. */
    @Autowired
    private UserDetailsServiceTMS userDetailsService;

    /** * Создает экземпляр менеджера аутентификации. *
     * @param authenticationConfiguration конфигурация аутентификации.
     * @return менеджер аутентификации.
     * @throws Exception если возникла ошибка при создании менеджера аутентификации. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /** * Настраивает цепочку фильтров безопасности. *
     * @param http объект для построения конфигурации безопасности HTTP.
     * @return настроенная цепочка фильтров безопасности.
     * @throws Exception если возникла ошибка при настройке цепочки фильтров. */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users/register").permitAll()
                        // Регистрация доступна всем
                        .requestMatchers(HttpMethod.GET,"/api/users").hasAuthority("ADMIN")
                        // Просмотр всех пользователей доступен только админу
                        .requestMatchers(HttpMethod.GET,"/api/users/{userId}").hasAuthority("ADMIN")
                        // Просмотр отдельного пользователя доступен только админу
                        .requestMatchers(HttpMethod.GET, "/api/tasks").hasAnyAuthority("USER", "ADMIN")
                        // Получение списка задач доступно пользователям и админам
                        .requestMatchers(HttpMethod.POST, "/api/tasks").hasAnyAuthority("USER", "ADMIN")
                        // Создание новой задачи доступно пользователям и админам
                        .requestMatchers(HttpMethod.PUT, "/api/tasks/{id}").hasAnyAuthority("USER", "ADMIN")
                        // Редактирование задачи доступно пользователям и админам
                        .requestMatchers(HttpMethod.DELETE, "/api/tasks/{id}").hasAuthority("ADMIN")
                        // Удаление задачи доступно только админу
                        .requestMatchers("/api/tasks/{taskId}/comments").hasAnyAuthority("USER", "ADMIN")
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