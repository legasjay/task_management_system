package com.olegandreevich.tms.dto;

import com.olegandreevich.tms.entities.enums.Priority;
import com.olegandreevich.tms.entities.enums.Status;

import java.util.Objects;

public class TaskDTO {
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private Long authorId;
    private Long assigneeId;

    public TaskDTO(String title, String description, Status status, Priority priority, Long authorId, Long assigneeId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.authorId = authorId;
        this.assigneeId = assigneeId;
    }

    public TaskDTO() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
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

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskDTO)) return false;
        TaskDTO taskDTO = (TaskDTO) o;
        return Objects.equals(getTitle(), taskDTO.getTitle()) &&
                Objects.equals(getDescription(), taskDTO.getDescription()) &&
                getStatus() == taskDTO.getStatus() &&
                getPriority() == taskDTO.getPriority() &&
                Objects.equals(getAuthorId(), taskDTO.getAuthorId()) &&
                Objects.equals(getAssigneeId(), taskDTO.getAssigneeId());
    }

    // Реализация hashCode
    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getDescription(), getStatus(), getPriority(), getAuthorId(), getAssigneeId());
    }
}
