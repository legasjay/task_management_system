package com.olegandreevich.tms.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.olegandreevich.tms.dto.TaskDTO;
import com.olegandreevich.tms.dto.TaskDTOGet;
import com.olegandreevich.tms.entities.Task;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.entities.enums.Priority;
import com.olegandreevich.tms.entities.enums.Role;
import com.olegandreevich.tms.entities.enums.Status;
import com.olegandreevich.tms.servicies.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    // Моки пользователей
    User user1 = new User(1L, "111@mail.ru", "user1", "pass", Role.ROLE_ADMIN, null, null);
    User user2 = new User(2L, "222@mail.ru", "user2", "pass", Role.ROLE_USER, null, null);
    User user3 = new User(3L, "333@mail.ru", "user3", "pass", Role.ROLE_USER, null, null);

    // Моки задач
    Task task1 = new Task(1L, "title1", "desc1", Status.PENDING, Priority.HIGH, user1, user1, new ArrayList<>());
    Task task2 = new Task(2L, "title2", "desc2", Status.COMPLETED, Priority.LOW, user2, user2, new ArrayList<>());
    Task task3 = new Task(3L, "title3", "desc3", Status.IN_PROGRESS, Priority.MEDIUM, user1, user3, new ArrayList<>());

    // Моки DTO
    TaskDTO taskDTO1 = new TaskDTO("title1", "desc1", Status.PENDING, Priority.HIGH, 1L, 1L);
    TaskDTO taskDTO2 = new TaskDTO("title2", "desc2", Status.COMPLETED, Priority.LOW, 2L, 2L);
    TaskDTO taskDTO3 = new TaskDTO("title3", "desc3", Status.IN_PROGRESS, Priority.MEDIUM, 1L, 3L);

    // Моки DTO
    TaskDTOGet taskDTOget1 = new TaskDTOGet(1L,"title1", "desc1", Status.PENDING, Priority.HIGH, 1L, 1L);
    TaskDTOGet taskDTOget2 = new TaskDTOGet(1L,"title2", "desc2", Status.COMPLETED, Priority.LOW, 2L, 2L);
    TaskDTOGet taskDTOget3 = new TaskDTOGet(1L,"title3", "desc3", Status.IN_PROGRESS, Priority.MEDIUM, 1L, 3L);

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    // Тестирование GET /api/tasks
    @Test
    void testGetTasks() throws Exception {
        // Подготовка данных
        List<TaskDTOGet> expectedTasks = Arrays.asList(taskDTOget1, taskDTOget2, taskDTOget3);

        // Создание страницы с результатами
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<TaskDTOGet> resultPage = new PageImpl<>(expectedTasks, pageable, expectedTasks.size());

        // Настройка мока
        when(taskService.getTasks(anyInt(), anyInt(), any(Sort.Direction.class), anyString()))
                .thenReturn(resultPage);

        // Выполнение запроса
        mockMvc.perform(get("/api/tasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("title1"))
                .andExpect(jsonPath("$[1].title").value("title2"))
                .andExpect(jsonPath("$[2].title").value("title3"));
    }

    // Тестирование POST /api/tasks
    @Test
    void testCreateTask() throws Exception {
        // Подготовка данных
        String requestJson = objectMapper.writeValueAsString(taskDTO1);

        // Настройка моков
        when(taskService.createTask(any(TaskDTO.class))).thenReturn(taskDTO1);

        // Выполнение запроса
        mockMvc.perform(post("/api/tasks")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("title1"));
    }

    // Тестирование PUT /api/tasks/{id}
    @Test
    void testUpdateTask() throws Exception {
        // Подготовка данных
        String requestJson = objectMapper.writeValueAsString(taskDTO1);

        // Настройка моков
        when(taskService.updateTask(eq(1L), any(TaskDTO.class))).thenReturn(taskDTO1);

        // Выполнение запроса
        mockMvc.perform(put("/api/tasks/1")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("title1"));
    }

    // Тестирование DELETE /api/tasks/{id}
    @Test
    void testDeleteTask() throws Exception {
        // Настройка моков
        doNothing().when(taskService).deleteTask(1L);

        // Выполнение запроса
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    // Тестирование GET /api/tasks/{id}
    @Test
    void testGetTaskById() throws Exception {
        // Настройка моков
        when(taskService.getTaskById(1L)).thenReturn(taskDTO1);

        // Выполнение запроса
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("title1"));
    }

    // Тестирование GET /api/tasks/author/{authorId}
    @Test
    void testFindTasksByAuthorId() throws Exception {
        // Подготовка данных
        List<TaskDTO> expectedTasks = Arrays.asList(taskDTO1, taskDTO3);

        // Настройка моков
        when(taskService.findTasksByAuthorId(1L)).thenReturn(expectedTasks);

        // Выполнение запроса
        mockMvc.perform(get("/api/tasks/author/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("title1"))
                .andExpect(jsonPath("$[1].title").value("title3"));
    }

    // Тестирование GET /api/tasks/assignee/{assigneeId}
    @Test
    void testFindTasksByAssigneeId() throws Exception {
        // Подготовка данных
        List<TaskDTO> expectedTasks = Arrays.asList(taskDTO3);

        // Настройка моков
        when(taskService.findTasksByAssigneeId(3L)).thenReturn(expectedTasks);

        // Выполнение запроса
        mockMvc.perform(get("/api/tasks/assignee/3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].title").value("title3"));
    }
}