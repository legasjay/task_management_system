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

    /** * Конструктор класса TaskController. * * @param taskService Сервис для работы с задачами. */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /** * Получение списка задач с учетом параметров пагинации и сортировки. *
     * @param page Номер страницы.
     * @param size Размер страницы. * @param direction Направление сортировки.
     * @param sortField Поле для сортировки. * @return Список задач. */
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

    /** * Создание новой задачи на основе предоставленных данных. *
     * @param taskDTO Данные для создания задачи.
     * @return Созданная задача. */
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

    /** * Обновление существующей задачи. *
     * @param id Идентификатор задачи.
     * @param taskDTO Новые данные для задачи.
     * @return Обновленная задача.
     * @throws ResourceNotFoundException Если задача не найдена. */
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
    ) throws ResourceNotFoundException {
        return taskService.updateTask(id, taskDTO);
    }

    /** * Удаление задачи. *
     * @param id Идентификатор задачи.
     * @throws ResourceNotFoundException Если задача не найдена. */
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

    /** * Получение задачи по идентификатору. *
     * @param id Идентификатор задачи.
     * @return Задача с указанным идентификатором.
     * @throws ResourceNotFoundException Если задача не найдена. */
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

    /** * Поиск задач по идентификатору автора. *
     * @param authorId Идентификатор автора.
     * @return Список задач, созданных автором. */
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

    /** * Поиск задач по идентификатору исполнителя. *
     * @param assigneeId Идентификатор исполнителя.
     * @return Список задач, назначенных исполнителю. */
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

    /** * Изменение статуса задачи. *
     * @param id Идентификатор задачи.
     * @param status Новый статус задачи.
     * @return Задача с измененным статусом. */
    @PutMapping("/{id}/{status}")
    @Operation(summary = "Изменение статуса задачи",
            description = "Изменяет статус задачи с указанным идентификатором.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Статус задачи успешно изменен"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    public TaskDTOGet changeTaskStatus(
            @Parameter(description = "Идентификатор задачи") @PathVariable Long id,
            @Parameter(description = "Новый статус задачи") @PathVariable String status
    ) {
        return taskService.changeTaskStatus(id, status);
    }
}
