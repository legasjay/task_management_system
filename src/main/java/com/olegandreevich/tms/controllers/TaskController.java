package com.olegandreevich.tms.controllers;

import com.olegandreevich.tms.dto.TaskDTO;
import com.olegandreevich.tms.dto.TaskDTOGet;
import com.olegandreevich.tms.servicies.TaskService;
import com.olegandreevich.tms.util.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskDTOGet> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "id") String sortField
    ) {
        return taskService.getTasks(page, size, direction, sortField).getContent();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO createTask(@Valid @RequestBody TaskDTO taskDTO) {
        return taskService.createTask(taskDTO);
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO)
            throws ResourceNotFoundException {
        return taskService.updateTask(id, taskDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) throws ResourceNotFoundException {
        taskService.deleteTask(id);
    }

    @GetMapping("/{id}")
    public TaskDTO getTaskById(@PathVariable Long id) throws ResourceNotFoundException {
        return taskService.getTaskById(id);
    }

    @GetMapping("/author/{authorId}")
    public List<TaskDTO> findTasksByAuthorId(@PathVariable Long authorId) {
        return taskService.findTasksByAuthorId(authorId);
    }

    @GetMapping("/assignee/{assigneeId}")
    public List<TaskDTO> findTasksByAssigneeId(@PathVariable Long assigneeId) {
        return taskService.findTasksByAssigneeId(assigneeId);
    }

    @PutMapping("/{id}/{status}")
    public TaskDTOGet changeTaskStatus(@PathVariable Long id, @PathVariable String status) {
        return taskService.changeTaskStatus(id, status);
    }
}
