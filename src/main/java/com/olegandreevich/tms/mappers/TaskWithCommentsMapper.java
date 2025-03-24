package com.olegandreevich.tms.mappers;

import com.olegandreevich.tms.dto.CommentDTO;
import com.olegandreevich.tms.dto.TaskWithCommentsDTO;
import com.olegandreevich.tms.entities.Task;
import com.olegandreevich.tms.servicies.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TaskWithCommentsMapper {

    @Autowired
    private CommentService commentService;

    /** * Преобразование Task в TaskWithCommentsDTO. */
    public TaskWithCommentsDTO toDtoWithComments(Task task) {
        TaskWithCommentsDTO taskWithCommentsDTO = new TaskWithCommentsDTO();

        taskWithCommentsDTO.setId(task.getId());
        taskWithCommentsDTO.setTitle(task.getTitle());
        taskWithCommentsDTO.setDescription(task.getDescription());
        taskWithCommentsDTO.setStatus(task.getStatus());
        taskWithCommentsDTO.setPriority(task.getPriority());
        taskWithCommentsDTO.setAuthorId(task.getAuthor().getId()); // Добавляем авторский ID
        taskWithCommentsDTO.setAssigneeId(task.getAssignee().getId()); // Добавляем ID назначенного

        taskWithCommentsDTO.setComments(commentService.getCommentsForTask(task.getId()).stream()
                .map(comment -> new CommentDTO(comment.getContent()))
                .collect(Collectors.toList())); // Получаем комментарии через сервис

        return taskWithCommentsDTO;
    }
}
