package com.olegandreevich.tms.servicies;

import com.olegandreevich.tms.dto.TaskDTO;
import com.olegandreevich.tms.dto.TaskDTOGet;
import com.olegandreevich.tms.dto.TaskWithCommentsDTO;
import com.olegandreevich.tms.entities.Task;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.mappers.TaskMapper;
import com.olegandreevich.tms.mappers.TaskMapperGet;
import com.olegandreevich.tms.mappers.TaskWithCommentsMapper;
import com.olegandreevich.tms.repositories.TaskRepository;
import com.olegandreevich.tms.repositories.UserRepository;
import com.olegandreevich.tms.util.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с задачами. * Предоставляет методы для создания, редактирования, удаления задач,
 * а также получения списков задач.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskMapperGet taskMapperGet;
    private final UserRepository userRepository;
    private final UserCheckService userCheckService;
    private final TaskWithCommentsMapper taskWithCommentsMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, TaskMapperGet taskMapperGet, UserRepository userRepository, UserCheckService userCheckService, TaskWithCommentsMapper taskWithCommentsMapper, CommentService commentService) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.taskMapperGet = taskMapperGet;
        this.userRepository = userRepository;
        this.userCheckService = userCheckService;
        this.taskWithCommentsMapper = taskWithCommentsMapper;
    }

    /**
     * Возвращает список задач с учетом заданных параметров пагинации и сортировки. * * @param page Номер страницы.
     *
     * @param size      Размер страницы. * @param direction Направление сортировки.
     * @param sortField Поле для сортировки. * @return Страница объектов DTO задач.
     * @throws AccessDeniedException если у пользователя нет прав администратора.
     */
    public Page<TaskDTOGet> getTasks(int page, int size, Sort.Direction direction, String sortField) {
        if (!userCheckService.isAdmin()) {
            throw new AccessDeniedException("У вас нет прав для получения всех задач.");
        }
        PageRequest pageRequest = PageRequest.of(page, size, direction, sortField);
        return taskRepository.findAll(pageRequest).map(taskMapperGet::toDto);
    }

    /**
     * Создает новую задачу. * * @param taskDTO DTO объекта задачи для создания. * @return DTO созданной задачи.
     *
     * @throws EntityNotFoundException если автор или исполнитель задачи не найдены.
     */
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

    /**
     * Обновляет существующую задачу. *
     *
     * @param id      ID задачи для обновления.
     * @param taskDTO DTO объекта задачи с новыми данными.
     * @return DTO обновленной задачи.
     * @throws ResourceNotFoundException если задача с указанным ID не найдена.
     */
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) throws ResourceNotFoundException, BadRequestException {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!userCheckService.isAdmin()) { // Если текущий пользователь не является администратором
            if (!userCheckService.isAssignee(existingTask.getAssignee().getId())) { // И не является исполнителем задачи
                throw new AccessDeniedException("У вас нет прав на обновление этой задачи.");
            }
            if (taskDTO.getTitle() != null && !taskDTO.getTitle().equals(existingTask.getTitle())) {
                throw new BadRequestException("Вы не можете изменить название задачи.");
            }
            if (taskDTO.getDescription() != null && !taskDTO.getDescription().equals(existingTask.getDescription())) {
                throw new BadRequestException("Вы не можете изменить описание задачи.");
            }
            if (taskDTO.getPriority() != null && !taskDTO.getPriority().equals(existingTask.getPriority())) {
                throw new BadRequestException("Вы не можете изменить приоритет задачи.");
            }
        }

        // Обновляем задачу
        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setStatus(taskDTO.getStatus());
        existingTask.setPriority(taskDTO.getPriority());
        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.toDto(updatedTask);
    }

    /**
     * Удаляет задачу по указанному ID. *
     *
     * @param id ID задачи для удаления.
     * @throws ResourceNotFoundException если задача с указанным ID не найдена.
     */
    public void deleteTask(Long id) throws ResourceNotFoundException {
        if (!userCheckService.isAdmin()) {
            throw new AccessDeniedException("У вас нет прав для удаления задачи.");
        }
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(existingTask);
    }

    /**
     * Возвращает задачу по указанному ID. *
     *
     * @param id ID задачи.
     * @return DTO задачи.
     * @throws ResourceNotFoundException если задача с указанным ID не найдена.
     * @throws AccessDeniedException     если у пользователя нет доступа к задаче.
     */
    public TaskDTO getTaskById(Long id) throws ResourceNotFoundException {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!userCheckService.isAssignee(existingTask.getAssignee().getId()) && !userCheckService.isAdmin()) {
            throw new AccessDeniedException("У вас нет прав для получения этой задачи.");
        }
        return taskMapper.toDto(existingTask);
    }

    /**
     * Возвращает список задач по ID автора. *
     *
     * @param authorId ID автора задач.
     * @return Список DTO задач.
     * @throws AccessDeniedException если у пользователя нет доступа к задачам данного автора.
     */
    public List<TaskDTO> findTasksByAuthorId(Long authorId) {
        Long currentUserId = userCheckService.getCachedUserId();
        if (!authorId.equals(currentUserId) && !userCheckService.isAdmin()) {
            throw new AccessDeniedException("Доступ запрещен.");
        }

        return taskRepository.findByAuthor_Id(authorId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список задач по ID исполнителя. *
     *
     * @param assigneeId ID исполнителя задач.
     * @return Список DTO задач.
     * @throws AccessDeniedException если у пользователя нет доступ к задачам данного исполнителя.
     */
    public List<TaskDTO> findTasksByAssigneeId(Long assigneeId) {
        Long currentUserId = userCheckService.getCachedUserId();
        if (!assigneeId.equals(currentUserId) && !userCheckService.isAdmin()) {
            throw new AccessDeniedException("Доступ запрещен.");
        }

        return taskRepository.findByAssignee_Id(assigneeId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список задач по ID исполнителя и комментариев к ним *
     *
     * @param assigneeId ID исполнителя задач.
     * @return Список DTO задач.
     * @throws AccessDeniedException если у пользователя нет доступ к задачам данного исполнителя.
     */
    public List<TaskWithCommentsDTO> findTasksByAssigneeIdWithComments(Long assigneeId) {
        Long currentUserId = userCheckService.getCachedUserId();
        if (!assigneeId.equals(currentUserId) && !userCheckService.isAdmin()) {
            throw new AccessDeniedException("Доступ запрещен.");
        }

        List<Task> tasks = taskRepository.findByAssignee_Id(assigneeId);
        return tasks.stream()
                .map(taskWithCommentsMapper::toDtoWithComments)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список задач по ID автора и комментариев к ним *
     *
     * @param authorId ID автора задач.
     * @return Список DTO задач.
     * @throws AccessDeniedException если у пользователя нет доступа к задачам данного автора.
     */
    public List<TaskWithCommentsDTO> findTasksByAuthorIdWithComments(Long authorId) {
        Long currentUserId = userCheckService.getCachedUserId();
        if (!authorId.equals(currentUserId) && !userCheckService.isAdmin()) {
            throw new AccessDeniedException("Доступ запрещен.");
        }

        List<Task> tasks = taskRepository.findByAuthor_Id(authorId);
        return tasks.stream()
                .map(taskWithCommentsMapper::toDtoWithComments)
                .collect(Collectors.toList());
    }
}

