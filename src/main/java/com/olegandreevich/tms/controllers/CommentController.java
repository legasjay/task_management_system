package com.olegandreevich.tms.controllers;

import com.olegandreevich.tms.dto.CommentDTO;
import com.olegandreevich.tms.entities.Comment;
import com.olegandreevich.tms.servicies.CommentService;
import com.olegandreevich.tms.util.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@Tag(name = "Комментарии", description = "Операции с комментариями к задачам")
@Validated
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавление комментария к задаче",
            description = "Добавляет новый комментарий к указанной задаче.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Комментарий успешно создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные комментария"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    public CommentDTO addComment(
            @Parameter(description = "Идентификатор задачи") @PathVariable Long taskId,
            @Parameter(description = "Идентификатор пользователя") @PathVariable Long userId,
            @Parameter(description = "Данные для добавления комментария") @Valid @RequestBody CommentDTO commentDTO
    ) throws ResourceNotFoundException {
        return commentService.addComment(taskId, userId, commentDTO);
    }

    /** * Получение списка комментариев для задачи. *
     * @param taskId Идентификатор задачи.
     * @return Список комментариев для указанной задачи. */
    @GetMapping
    @Operation(summary = "Получение списка комментариев для задачи",
            description = "Возвращает список всех комментариев для указанной задачи.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список комментариев успешно получен"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    public List<CommentDTO> getCommentsForTask(
            @Parameter(description = "Идентификатор задачи") @PathVariable Long taskId
    ) {
        return commentService.getCommentsForTask(taskId);
    }

    /** * Удаление комментария. *
     * @param commentId Идентификатор комментария.
     * @throws ResourceNotFoundException Если комментарий не найден. */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление комментария",
            description = "Удаляет комментарий с указанным идентификатором.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Комментарий успешно удален"),
                    @ApiResponse(responseCode = "404", description = "Комментарий не найден")
            })
    public void deleteComment(
            @Parameter(description = "Идентификатор комментария") @PathVariable Long commentId
    ) throws ResourceNotFoundException {
        commentService.deleteComment(commentId);
    }

    /** * Получение всех комментариев. *
     * @return Список всех комментариев.
     */
    @GetMapping("/")
    @Operation(summary = "Получение всех комментариев",
            description = "Возвращает список всех существующих комментариев.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список комментариев успешно получен")
            })
    public List<Comment> getAllComments() {
        return commentService.getAllComments();
    }
}
