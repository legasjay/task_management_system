package com.olegandreevich.tms.entities;

import com.olegandreevich.tms.entities.enums.Priority;
import com.olegandreevich.tms.entities.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")

public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Заголовок задачи не может быть пустым.")
    @Size(min = 3, max = 100, message = "Длина заголовка должна быть от 3 до 100 символов.")
    private String title;

    @Column(columnDefinition = "TEXT")
    @Size(max = 500, message = "Максимальная длина описания - 500 символов.")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Task(Long taskId) {
        this.id = taskId;
    }

    public Task(Long id, String title, String description, Status status, Priority priority, User author, User assignee, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.author = author;
        this.assignee = assignee;
        this.comments = comments;
    }

    public Task() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Заголовок задачи не может быть пустым.") @Size(min = 3, max = 100, message = "Длина заголовка должна быть от 3 до 100 символов.") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "Заголовок задачи не может быть пустым.") @Size(min = 3, max = 100, message = "Длина заголовка должна быть от 3 до 100 символов.") String title) {
        this.title = title;
    }

    public @Size(max = 500, message = "Максимальная длина описания - 500 символов.") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 500, message = "Максимальная длина описания - 500 символов.") String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}


