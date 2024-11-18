package com.olegandreevich.tms.servicies;

import com.olegandreevich.tms.dto.TaskDTO;
import com.olegandreevich.tms.dto.TaskDTOGet;
import com.olegandreevich.tms.entities.Task;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.mappers.TaskMapper;
import com.olegandreevich.tms.mappers.TaskMapperGet;
import com.olegandreevich.tms.repositories.TaskRepository;
import com.olegandreevich.tms.repositories.UserRepository;
import com.olegandreevich.tms.util.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskMapperGet taskMapperGet;
    private final UserRepository userRepository;

    public Page<TaskDTOGet> getTasks(int page, int size, Sort.Direction direction, String sortField) {
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
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.toDto(task);
    }

    public List<TaskDTO> findTasksByAuthorId(Long authorId) {
        return taskRepository.findByAuthor_Id(authorId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> findTasksByAssigneeId(Long assigneeId) {
        return taskRepository.findByAssignee_Id(assigneeId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
}
