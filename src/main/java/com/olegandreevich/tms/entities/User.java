package com.olegandreevich.tms.entities;

import com.olegandreevich.tms.entities.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;



@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Pattern(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$",
            message = "Неверный формат электронной почты")
    private String email;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "author")
    private Set<Task> tasksAsAuthor;

    @OneToMany(mappedBy = "assignee")
    private Set<Task> tasksAsAssignee;

    @Builder
    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = Role.USER;
    }

    public User(Long userId) {
        this.id = userId;
    }

    public static User of(String email, String password) {
        return builder()
                .email(email)
                .password(password)
                .role(Role.USER)
                .build();
    }

    public boolean isAdmin() {
        return Role.ADMIN.equals(role);
    }

    public void promoteToAdmin() {
        this.role = Role.ADMIN;
    }

    public void demoteToUser() {
        this.role = Role.USER;
    }

}
