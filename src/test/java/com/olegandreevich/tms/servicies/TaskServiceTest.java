package com.olegandreevich.tms.servicies;

import com.olegandreevich.tms.dto.CommentDTO;
import com.olegandreevich.tms.dto.TaskDTO;
import com.olegandreevich.tms.dto.TaskDTOGet;
import com.olegandreevich.tms.dto.TaskWithCommentsDTO;
import com.olegandreevich.tms.entities.Comment;
import com.olegandreevich.tms.entities.Task;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.entities.enums.Priority;
import com.olegandreevich.tms.entities.enums.Role;
import com.olegandreevich.tms.entities.enums.Status;
import com.olegandreevich.tms.mappers.TaskMapper;
import com.olegandreevich.tms.mappers.TaskMapperGet;
import com.olegandreevich.tms.mappers.TaskWithCommentsMapper;
import com.olegandreevich.tms.repositories.TaskRepository;
import com.olegandreevich.tms.repositories.UserRepository;
import com.olegandreevich.tms.util.exceptions.ResourceNotFoundException;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;
    @Mock
    private UserCheckService userCheckService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private TaskMapperGet taskMapperGet;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentService commentService;
    @Mock
    private TaskWithCommentsMapper taskWithCommentsMapper;

    // Моки пользователей
    User user1 = new User(1L, "111@mail.ru", "user1", "pass",
            Role.ADMIN, null, null);
    User user2 = new User(2L, "222@mail.ru", "user2", "pass",
            Role.USER, null, null);
    User user3 = new User(3L, "333@mail.ru", "user3", "pass",
            Role.USER, null, null);

    // Моки задач
    Task task1 = new Task(1L, "title1", "desc1",
            Status.PENDING, Priority.HIGH, user1, user1, new ArrayList<>());
    Task task2 = new Task(2L, "title2", "desc2",
            Status.COMPLETED, Priority.LOW, user2, user2, new ArrayList<>());
    Task task3 = new Task(3L, "title3", "desc3",
            Status.IN_PROGRESS, Priority.MEDIUM, user1, user3, new ArrayList<>());

    // Моки DTO
    TaskDTO taskDTO1 = new TaskDTO("title1", "desc1",
            Status.PENDING, Priority.HIGH, 1L, 1L);
    TaskDTO taskDTO2 = new TaskDTO("title2", "desc2",
            Status.COMPLETED, Priority.LOW, 2L, 2L);
    TaskDTO taskDTO3 = new TaskDTO("title3", "desc3",
            Status.IN_PROGRESS, Priority.MEDIUM, 1L, 3L);
    // Моки DTO
    TaskDTOGet taskDTOget1 = new TaskDTOGet(1L, "title1", "desc1",
            Status.PENDING, Priority.MEDIUM, 1L, 1L);
    TaskDTOGet taskDTOget2 = new TaskDTOGet(1L, "title2", "desc2",
            Status.COMPLETED, Priority.LOW, 2L, 2L);
    TaskDTOGet taskDTOget3 = new TaskDTOGet(1L, "title3", "desc3",
            Status.IN_PROGRESS, Priority.MEDIUM, 1L, 3L);

    // Моки комментариев
    Comment comment1 = new Comment(1L, "comment1", user1, task1);
    Comment comment2 = new Comment(2L, "comment2", user2, task2);
    Comment comment3 = new Comment(3L, "comment3", user3, task3);

    // Моки DTO комментариев
    CommentDTO commentDTO1 = new CommentDTO("comment1");
    CommentDTO commentDTO2 = new CommentDTO("comment2");
    CommentDTO commentDTO3 = new CommentDTO("comment3");

    // Моки DTO задач с комментариями
    TaskWithCommentsDTO taskWithCommentsDTO1 = new TaskWithCommentsDTO(
            1L,
            "title1",
            "desc1",
            Status.PENDING,
            Priority.HIGH,
            1L,
            1L,
            Arrays.asList(commentDTO1)
    );

    // Моки DTO задач с комментариями
    TaskWithCommentsDTO taskWithCommentsDTO2 = new TaskWithCommentsDTO(
            2L,
            "title2",
            "desc2",
            Status.COMPLETED,
            Priority.LOW,
            2L,
            2L,
            Arrays.asList(commentDTO2)
    );

    /**
     * Тест получения всех задач с пагинацией
     */
    @Test
    void shouldReturnPaginatedTasks_whenPageSizeAndSortParamsProvided() {
        // Arrange
        int page = 0;
        int size = 10;
        Sort.Direction direction = Sort.Direction.ASC;
        String sortField = "title";

        List tasks = Arrays.asList(task1, task2, task3);
        Page pagedTasks = new PageImpl<>(tasks);
        when(userCheckService.isAdmin()).thenReturn(true); // Мок администрирования
        when(taskRepository.findAll(PageRequest.of(page, size, direction, sortField))).thenReturn(pagedTasks);
        when(taskMapperGet.toDto(any(Task.class))).thenReturn(taskDTOget1, taskDTOget2, taskDTOget3);

        // Act
        Page result = taskService.getTasks(page, size, direction, sortField);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements()); // Убедитесь, что количество задач верное
        verify(taskRepository).findAll(PageRequest.of(page, size, direction, sortField));
    }

    /**
     * Тест создания новой задачи
     */
    @Test
    void shouldCreateNewTask_andMapItCorrectly() {
        // Настройки мока репозитория
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        lenient().when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        lenient().when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        // Настройка мока для мапперов
        when(taskMapper.toEntity(any(TaskDTO.class))).thenAnswer(i -> {
            TaskDTO dto = i.getArgument(0);
            return new Task(14L, dto.getTitle(), dto.getDescription(),
                    dto.getStatus(), dto.getPriority(), user1, user2, new ArrayList<>());
        });

        when(taskMapper.toDto(any(Task.class))).thenAnswer(i -> {
            Task task = i.getArgument(0);
            return new TaskDTO(task.getTitle(), task.getDescription(),
                    task.getStatus(), task.getPriority(), task.getAuthor().getId(), task.getAssignee().getId());
        });

        // Вызов метода сервиса
        TaskDTO createdTaskDTO = taskService.createTask(taskDTO1);

        // Проверка результата
        assertEquals(createdTaskDTO, taskDTO1);
    }

    /**
     * Тест обновления существующей задачи
     */
    @Test
    void shouldUpdateExistingTask_andReturnUpdatedDTO() throws ResourceNotFoundException, BadRequestException {
        // Настройка моков
        when(userCheckService.isAdmin()).thenReturn(true);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        when(taskMapper.toDto(any(Task.class))).thenAnswer(i -> {
            Task task = i.getArgument(0);
            return new TaskDTO(task.getTitle(), task.getDescription(),
                    task.getStatus(), task.getPriority(), task.getAuthor().getId(), task.getAssignee().getId());
        });

        // Вызов метода сервиса
        TaskDTO updatedTaskDTO = taskService.updateTask(1L, taskDTO1);

        // Проверки
        assertEquals(updatedTaskDTO, taskDTO1);
    }

    /**
     * Тест удаления задачи
     */
    @Test
    void shouldDeleteExistingTask() throws ResourceNotFoundException {

        when(userCheckService.isAdmin()).thenReturn(true);
        // Настройки мока репозитория
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        // Вызов метода сервиса
        taskService.deleteTask(1L);

        // Проверка вызова удаления
        verify(taskRepository).delete(task1);
    }

    /**
     * Тест получения задачи по ID
     */
    @Test
    void shouldReturnTaskDTO_whenTaskFoundById() throws ResourceNotFoundException {
        // Настройки мока репозитория
        when(userCheckService.isAssignee(1L)).thenReturn(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskMapper.toDto(any(Task.class))).thenAnswer(i -> {
            Task task = i.getArgument(0);
            return new TaskDTO(task.getTitle(), task.getDescription(),
                    task.getStatus(), task.getPriority(), task.getAuthor().getId(), task.getAssignee().getId());
        });

        // Вызов метода сервиса
        TaskDTO retrievedTaskDTO = taskService.getTaskById(1L);

        // Проверка результата
        assertEquals(retrievedTaskDTO, taskDTO1);
    }

    @Test
    void shouldReturnTasks_whenSearchedByAuthorId() {

        when(userCheckService.isAdmin()).thenReturn(true);
        when(taskRepository.findByAuthor_Id(1L)).thenReturn(Arrays.asList(task1));

        when(taskMapper.toDto(any(Task.class))).thenAnswer(i -> {
            Task task = i.getArgument(0);
            return new TaskDTO(task.getTitle(), task.getDescription(),
                    task.getStatus(), task.getPriority(), task.getAuthor().getId(), task.getAssignee().getId());
        });

        List<TaskDTO> tasksByAuthor = taskService.findTasksByAuthorId(1L);

        assertEquals(tasksByAuthor, Arrays.asList(taskDTO1));
    }

    @Test
    void shouldReturnTasks_whenSearchedByAssigneeId() {

        when(userCheckService.isAdmin()).thenReturn(true);
        when(taskRepository.findByAssignee_Id(2L)).thenReturn(Arrays.asList(task1));

        when(taskMapper.toDto(any(Task.class))).thenAnswer(i -> {
            Task task = i.getArgument(0);
            return new TaskDTO(task.getTitle(), task.getDescription(),
                    task.getStatus(), task.getPriority(), task.getAuthor().getId(), task.getAssignee().getId());
        });

        List<TaskDTO> tasksByAssignee = taskService.findTasksByAssigneeId(2L);

        assertEquals(tasksByAssignee, Arrays.asList(taskDTO1));
    }

    @Test
    void shouldReturnTasksWithComments_whenSearchedByAuthorId() {

        // Настройка мок-объекта userCheckService
        when(userCheckService.getCachedUserId()).thenReturn(1L);

        // Настройка мок-объекта taskRepository
        when(taskRepository.findByAuthor_Id(1L)).thenReturn(Arrays.asList(task1));

        // Настройка мок-объекта commentService
        when(commentService.getCommentsForTask(1L)).thenReturn(Arrays.asList(commentDTO1));

        // Выполнение метода
        List<TaskWithCommentsDTO> result = taskService.findTasksByAuthorIdWithComments(1L);

        // Утверждение
        assertEquals(result, Arrays.asList(taskWithCommentsDTO1));
    }

    @Test
    void shouldReturnTasksWithComments_whenSearchedByAssigneeId() {

        // Настройка мок-объекта userCheckService
        when(userCheckService.getCachedUserId()).thenReturn(2L);

        // Настройка мок-объекта taskRepository
        when(taskRepository.findByAssignee_Id(2L)).thenReturn(Arrays.asList(task2));

        // Настройка мок-объекта commentService
        when(commentService.getCommentsForTask(2L)).thenReturn(Arrays.asList(commentDTO2));

        // Выполнение метода
        List<TaskWithCommentsDTO> result = taskService.findTasksByAssigneeIdWithComments(2L);

        // Утверждение
        assertEquals(result, Arrays.asList(taskWithCommentsDTO2));
    }

    @Test
    void shouldThrowAccessDeniedException_whenNonAdminSearchesOtherUsersTasks() {
        // Настройка мок-объекта userCheckService
        when(userCheckService.getCachedUserId()).thenReturn(2L); // Текущий пользователь имеет ID 2
        when(userCheckService.isAdmin()).thenReturn(false);

        // Ожидаемое исключение
        AccessDeniedException exception = Assertions.assertThrows(AccessDeniedException.class, () -> {
            taskService.findTasksByAuthorIdWithComments(1L);
        });

        // Утверждение
        assertEquals(exception.getMessage(), "Доступ запрещен.");
    }
}