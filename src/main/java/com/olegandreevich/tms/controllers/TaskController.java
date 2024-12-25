package com.olegandreevich.tms.controllers;

import com.olegandreevich.tms.dto.TaskDTO;
import com.olegandreevich.tms.dto.TaskDTOGet;
import com.olegandreevich.tms.servicies.TaskService;
import com.olegandreevich.tms.util.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Задачи", description = "Операции с задачами")
@Validated
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Получение списка задач",
            description = "Возвращает список задач с учетом параметров пагинации и сортировки.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение списка задач"),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
            })
    public List<TaskDTOGet> getTasks(
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Направление сортировки") @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @Parameter(description = "Поле для сортировки") @RequestParam(defaultValue = "id") String sortField
    ) {
        return taskService.getTasks(page, size, direction, sortField).getContent();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание новой задачи",
            description = "Создает новую задачу на основе предоставленных данных.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные задачи")
            })
    public TaskDTO createTask(@Parameter(description = "Данные для создания задачи") @Valid @RequestBody TaskDTO taskDTO) {
        return taskService.createTask(taskDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление существующей задачи",
            description = "Обновляет задачу с указанным идентификатором.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача успешно обновлена"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    public TaskDTO updateTask(
            @Parameter(description = "Идентификатор задачи") @PathVariable Long id,
            @Parameter(description = "Новые данные для задачи") @Valid @RequestBody TaskDTO taskDTO
    ) throws BadRequestException {
        return taskService.updateTask(id, taskDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление задачи",
            description = "Удаляет задачу с указанным идентификатором.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    public void deleteTask(
            @Parameter(description = "Идентификатор задачи") @PathVariable Long id
    ) throws ResourceNotFoundException {
        taskService.deleteTask(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение задачи по идентификатору",
            description = "Возвращает задачу с указанным идентификатором.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача успешно получена"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    public TaskDTO getTaskById(
            @Parameter(description = "Идентификатор задачи") @PathVariable Long id
    ) throws ResourceNotFoundException {
        return taskService.getTaskById(id);
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Поиск задач по идентификатору автора",
            description = "Возвращает список задач, созданных автором с указанным идентификатором.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список задач успешно получен"),
                    @ApiResponse(responseCode = "404", description = "Автор не найден")
            })
    public List<TaskDTO> findTasksByAuthorId(
            @Parameter(description = "Идентификатор автора") @PathVariable Long authorId
    ) {
        return taskService.findTasksByAuthorId(authorId);
    }

    @GetMapping("/assignee/{assigneeId}")
    @Operation(summary = "Поиск задач по идентификатору исполнителя",
            description = "Возвращает список задач, назначенных исполнителю с указанным идентификатором.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список задач успешно получен"),
                    @ApiResponse(responseCode = "404", description = "Исполнитель не найден")
            })
    public List<TaskDTO> findTasksByAssigneeId(
            @Parameter(description = "Идентификатор исполнителя") @PathVariable Long assigneeId
    ) {
        return taskService.findTasksByAssigneeId(assigneeId);
    }
}
