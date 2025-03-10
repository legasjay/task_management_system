package com.olegandreevich.tms.mappers;

import com.olegandreevich.tms.dto.TaskWithCommentsDTO;
import com.olegandreevich.tms.entities.Task;
import com.olegandreevich.tms.servicies.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskWithCommentsMapper {

    @Autowired
    private CommentService commentService;

    /** * Преобразование Task в TaskWithCommentsDTO. */
    public TaskWithCommentsDTO toDtoWithComments(Task task) {
        TaskWithCommentsDTO taskWithCommentsDTO = new TaskWithCommentsDTO();

        // Базовые поля
        taskWithCommentsDTO.setTitle(task.getTitle());
        taskWithCommentsDTO.setDescription(task.getDescription());
        taskWithCommentsDTO.setStatus(task.getStatus());
        taskWithCommentsDTO.setPriority(task.getPriority());

        // Связанные пользователи
        if (task.getAuthor() != null) {
            taskWithCommentsDTO.setAuthorId(task.getAuthor().getId());
        }
        if (task.getAssignee() != null) {
            taskWithCommentsDTO.setAssigneeId(task.getAssignee().getId());
        }

        // Комментарии
        taskWithCommentsDTO.setComments(commentService.getCommentsForTask(task.getId()));

        return taskWithCommentsDTO;
    }
}
