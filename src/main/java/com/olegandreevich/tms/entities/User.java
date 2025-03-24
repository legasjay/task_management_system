package com.olegandreevich.tms.entities;

import com.olegandreevich.tms.entities.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import java.util.Set;

@Entity
@Table(name = "users")
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


    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = Role.USER;
    }

    public User() {

    }

    public User(Long userId) {
        this.id = userId;
    }

    public User(Long id, String email, String username, String password, Role role, Set<Task> tasksAsAuthor, Set<Task> tasksAsAssignee) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.tasksAsAuthor = tasksAsAuthor;
        this.tasksAsAssignee = tasksAsAssignee;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @Pattern(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$",
            message = "Неверный формат электронной почты") String getEmail() {
        return email;
    }

    public void setEmail(@Pattern(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$",
            message = "Неверный формат электронной почты") String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Task> getTasksAsAuthor() {
        return tasksAsAuthor;
    }

    public void setTasksAsAuthor(Set<Task> tasksAsAuthor) {
        this.tasksAsAuthor = tasksAsAuthor;
    }

    public Set<Task> getTasksAsAssignee() {
        return tasksAsAssignee;
    }

    public void setTasksAsAssignee(Set<Task> tasksAsAssignee) {
        this.tasksAsAssignee = tasksAsAssignee;
    }
}
