package com.olegandreevich.tms.servicies;

import com.olegandreevich.tms.dto.TaskDTO;
import com.olegandreevich.tms.dto.TaskDTOGet;
import com.olegandreevich.tms.entities.Task;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.entities.enums.Priority;
import com.olegandreevich.tms.entities.enums.Role;
import com.olegandreevich.tms.entities.enums.Status;
import com.olegandreevich.tms.mappers.TaskMapper;
import com.olegandreevich.tms.mappers.TaskMapperGet;
import com.olegandreevich.tms.repositories.TaskRepository;
import com.olegandreevich.tms.repositories.UserRepository;
import com.olegandreevich.tms.security.UserDetailsTMS;
import com.olegandreevich.tms.util.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private TaskMapperGet taskMapperGet;
    @Mock
    private UserRepository userRepository;

    // Моки пользователей
    User user1 = new User(1L, "111@mail.ru", "user1", "pass", Role.ADMIN, null, null);
    User user2 = new User(2L, "222@mail.ru", "user2", "pass", Role.USER, null, null);
    User user3 = new User(3L, "333@mail.ru", "user3", "pass", Role.USER, null, null);

    // Моки задач
    Task task1 = new Task(1L, "title1", "desc1", Status.PENDING, Priority.HIGH, user1, user1, new ArrayList<>());
    Task task2 = new Task(2L, "title2", "desc2", Status.COMPLETED, Priority.LOW, user2, user2, new ArrayList<>());
    Task task3 = new Task(3L, "title3", "desc3", Status.IN_PROGRESS, Priority.MEDIUM, user1, user3, new ArrayList<>());

    // Моки DTO
    TaskDTO taskDTO1 = new TaskDTO("title1", "desc1", Status.PENDING, Priority.HIGH, 1L, 1L);
    TaskDTO taskDTO2 = new TaskDTO("title2", "desc2", Status.COMPLETED, Priority.LOW, 2L, 2L);
    TaskDTO taskDTO3 = new TaskDTO("title3", "desc3", Status.IN_PROGRESS, Priority.MEDIUM, 1L, 3L);
    // Моки DTO
    TaskDTOGet taskDTOget1 = new TaskDTOGet(1L, "title1", "desc1", Status.PENDING, Priority.HIGH, 1L, 1L);
    TaskDTOGet taskDTOget2 = new TaskDTOGet(1L, "title2", "desc2", Status.COMPLETED, Priority.LOW, 2L, 2L);
    TaskDTOGet taskDTOget3 = new TaskDTOGet(1L, "title3", "desc3", Status.IN_PROGRESS, Priority.MEDIUM, 1L, 3L);


    @BeforeEach
    void setUp() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        UserDetailsTMS principal = new UserDetailsTMS(user1.getEmail(), user1.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ADMIN")));

        when(authentication.getPrincipal()).thenReturn(principal);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

         doReturn(Optional.of(user1)).when(userRepository).findByEmail(eq(user1.getEmail()));
        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail(user2.getEmail())).thenReturn(Optional.of(user2));
    }


    /**
     * Тест получения всех задач с пагинацией
     */
    @Test
    void shouldReturnPaginatedTasks_whenPageSizeAndSortParamsProvided() {
        // Arrange: подготовка данных
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.ASC, "id");

        // Настройка мока репозитория
        List<Task> tasks = Arrays.asList(task1, task2, task3);
        Page<Task> pageOfTasks = new PageImpl<>(tasks);
        when(taskRepository.findAll(pageRequest)).thenReturn(pageOfTasks);

        // Настройка мока для мапперов
        when(taskMapperGet.toDto(task1)).thenReturn(taskDTOget1);
        when(taskMapperGet.toDto(task2)).thenReturn(taskDTOget2);
        when(taskMapperGet.toDto(task3)).thenReturn(taskDTOget3);

        // Act: вызов метода сервиса
        Page<TaskDTOGet> result = taskService.getTasks(0, 10, Sort.Direction.ASC, "id");

        // Assert: проверки
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
        assertEquals(taskDTOget1, result.getContent().get(0));
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
    void shouldUpdateExistingTask_andReturnUpdatedDTO() throws ResourceNotFoundException {
        // Настройки мока репозитория
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);
        when(taskMapper.toDto(any(Task.class))).thenAnswer(i -> {
            Task task = i.getArgument(0);
            return new TaskDTO(task.getTitle(), task.getDescription(),
                    task.getStatus(), task.getPriority(), task.getAuthor().getId(), task.getAssignee().getId());
        });

        // Вызов метода сервиса
        TaskDTO updatedTaskDTO = taskService.updateTask(1L, taskDTO1);

        // Проверка результата
        assertEquals(updatedTaskDTO, taskDTO1);
    }

    /**
     * Тест удаления задачи
     */
    @Test
    void shouldDeleteExistingTask() throws ResourceNotFoundException {
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
        when(taskService.getCachedUserId()).thenReturn(1L); // Мок для текущего пользователя
        when(taskRepository.findByAuthor_Id(1L)).thenReturn(Arrays.asList(task1)); // Настроить мок-объект taskRepository

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
        when(taskService.getCachedUserId()).thenReturn(2L); // Мок для текущего пользователя
        when(taskRepository.findByAssignee_Id(2L)).thenReturn(Arrays.asList(task1)); // Настраиваем мок-объект taskRepository

        when(taskMapper.toDto(any(Task.class))).thenAnswer(i -> {
            Task task = i.getArgument(0);
            return new TaskDTO(task.getTitle(), task.getDescription(),
                    task.getStatus(), task.getPriority(), task.getAuthor().getId(), task.getAssignee().getId());
        });

        List<TaskDTO> tasksByAssignee = taskService.findTasksByAssigneeId(2L);

        assertEquals(tasksByAssignee, Arrays.asList(taskDTO1));
    }
}