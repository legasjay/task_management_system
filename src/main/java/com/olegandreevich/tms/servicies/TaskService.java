package com.olegandreevich.tms.servicies;

import com.olegandreevich.tms.dto.TaskDTO;
import com.olegandreevich.tms.dto.TaskDTOGet;
import com.olegandreevich.tms.entities.Task;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.entities.enums.Status;
import com.olegandreevich.tms.mappers.TaskMapper;
import com.olegandreevich.tms.mappers.TaskMapperGet;
import com.olegandreevich.tms.repositories.TaskRepository;
import com.olegandreevich.tms.repositories.UserRepository;
import com.olegandreevich.tms.security.UserDetailsTMS;
import com.olegandreevich.tms.util.exceptions.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskMapperGet taskMapperGet;
    private final UserRepository userRepository;

    private static final String CURRENT_USER_ID_CACHE_KEY = "current_user_id";
    private Long cachedUserId;

    @PostConstruct
    public void init() {
        this.cachedUserId = null;
    }

    @Cacheable(value = CURRENT_USER_ID_CACHE_KEY)
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetailsTMS) {
            String username = ((UserDetailsTMS) principal).getUsername();

            try {
                Optional<User> optionalUser = userRepository.findByEmail(username);

                if (optionalUser.isPresent()) {
                    return optionalUser.get().getId();
                } else {
                    throw new RuntimeException("Пользователь с таким email не найден.");
                }
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при получении текущего ID пользователя.", e);
            }
        }

        throw new RuntimeException("Не удалось получить текущий ID пользователя.");
    }

    public Long getCachedUserId() {
        if (cachedUserId == null) {
            cachedUserId = getCurrentUserId();
        }
        return cachedUserId;
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private boolean isTaskAssignee(Task task) {
        Long currentUserId = getCurrentUserId();
        return Objects.equals(currentUserId, task.getAssignee().getId());
    }

    public Page<TaskDTOGet> getTasks(int page, int size, Sort.Direction direction, String sortField) {
        if (!isAdmin()) {
            throw new AccessDeniedException("У вас нет прав для изменения статуса этой задачи.");
        }
        PageRequest pageRequest = PageRequest.of(page, size, direction, sortField);
        return taskRepository.findAll(pageRequest).map(taskMapperGet::toDto);
    }

    public TaskDTO createTask(TaskDTO taskDTO) {
        User author = userRepository.findById(taskDTO.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));
        User assignee = userRepository.findById(taskDTO.getAssigneeId())
                .orElseThrow(() -> new EntityNotFoundException("Assignee not found"));

        Task task = taskMapper.toEntity(taskDTO);
        task.setAuthor(author);
        task.setAssignee(assignee);

        Task savedTask = taskRepository.save(task);

        return taskMapper.toDto(savedTask);
    }

    public TaskDTO updateTask(Long id, TaskDTO taskDTO) throws ResourceNotFoundException {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setStatus(taskDTO.getStatus());
        existingTask.setPriority(taskDTO.getPriority());
        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.toDto(updatedTask);
    }

    public void deleteTask(Long id) throws ResourceNotFoundException {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(existingTask);
    }

    public TaskDTO getTaskById(Long id) throws ResourceNotFoundException {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!isTaskAssignee(existingTask) && !isAdmin()) {
            throw new AccessDeniedException("У вас нет прав для изменения статуса этой задачи.");
        }
        return taskMapper.toDto(existingTask);
    }

    public List<TaskDTO> findTasksByAuthorId(Long authorId) {
        Long currentUserId = getCachedUserId();
        if (!authorId.equals(currentUserId) && !isAdmin()) {
            throw new AccessDeniedException("Доступ запрещен.");
        }

        return taskRepository.findByAuthor_Id(authorId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> findTasksByAssigneeId(Long assigneeId) {
        Long currentUserId = getCachedUserId();
        if (!assigneeId.equals(currentUserId) && !isAdmin()) {
            throw new AccessDeniedException("Доступ запрещен.");
        }

        return taskRepository.findByAssignee_Id(assigneeId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }


    public TaskDTOGet changeTaskStatus(Long id, String status) throws ResourceNotFoundException {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!isTaskAssignee(existingTask) && !isAdmin()) {
            throw new AccessDeniedException("У вас нет прав для изменения статуса этой задачи.");
        }

        try {
            Status newStatus = Status.valueOf(status);
            existingTask.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверное значение статуса: " + status);
        }

        Task updatedTask = taskRepository.save(existingTask);

        return taskMapperGet.toDto(updatedTask);
    }
}
