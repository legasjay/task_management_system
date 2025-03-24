package com.olegandreevich.tms.dto;

import com.olegandreevich.tms.entities.enums.Priority;
import com.olegandreevich.tms.entities.enums.Status;

import java.util.List;
import java.util.Objects;


public class TaskWithCommentsDTO extends TaskDTOGet {

    private List<CommentDTO> comments;

    public TaskWithCommentsDTO() {

    }

    public TaskWithCommentsDTO(Long id, String title, String description, Status status, Priority priority,
                               Long authorId, Long assigneeId, List<CommentDTO> comments) {
        super(id, title, description, status, priority, authorId, assigneeId);
        this.comments = comments;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskWithCommentsDTO that = (TaskWithCommentsDTO) o;
        return Objects.equals(comments, that.comments) &&
                super.equals(o); // Вызываем equals родительского класса
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), comments);
    }
}
