package com.olegandreevich.tms.controllers;

import com.olegandreevich.tms.dto.CommentDTO;
import com.olegandreevich.tms.servicies.CommentService;
import com.olegandreevich.tms.util.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO addComment(@PathVariable Long taskId,
                                 @PathVariable Long userId,
                                 @Valid @RequestBody CommentDTO commentDTO)
            throws ResourceNotFoundException {
        return commentService.addComment(taskId, userId, commentDTO);
    }

    @GetMapping()
    public List<CommentDTO> getCommentsForTask(@PathVariable Long taskId) {
        return commentService.getCommentsForTask(taskId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) throws ResourceNotFoundException {
        commentService.deleteComment(commentId);
    }
}
