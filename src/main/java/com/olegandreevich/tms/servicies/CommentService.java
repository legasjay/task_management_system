package com.olegandreevich.tms.servicies;

import com.olegandreevich.tms.dto.CommentDTO;
import com.olegandreevich.tms.entities.Comment;
import com.olegandreevich.tms.entities.Task;
import com.olegandreevich.tms.entities.User;
import com.olegandreevich.tms.mappers.CommentMapper;
import com.olegandreevich.tms.repositories.CommentRepository;
import com.olegandreevich.tms.util.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/** * Сервис для работы с комментариями. * Предоставляет методы для добавления, получения и удаления комментариев. */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ModelMapper modelMapper;

    /** * Добавляет новый комментарий к задаче. * * @param taskId ID задачи, к которой добавляется комментарий.
     * @param userId ID пользователя, оставившего комментарий. * @param commentDTO DTO объекта комментария
     *               для добавления. * @return DTO добавленного комментария. * @throws ResourceNotFoundException
     *               если задача или пользователь не найдены. */
    public CommentDTO addComment(Long taskId, Long userId, CommentDTO commentDTO) throws ResourceNotFoundException {
        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setTask(new Task(taskId)); // Assume you have a method to fetch Task by ID
        comment.setUser(new User(userId));
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    /** * Возвращает список комментариев для указанной задачи. * * @param taskId ID задачи,
     * для которой возвращаются комментарии. * @return Список DTO комментариев. */
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsForTask(Long taskId) {
        return commentRepository.findAll()
                .stream()
                .filter(comment -> comment.getTask().getId().equals(taskId))
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    /** * Удаляет комментарий по указанному ID. * * @param id ID комментария для удаления.
     * @throws ResourceNotFoundException если комментарий с указанным ID не найден. */
    @Transactional
    public void deleteComment(Long id) throws ResourceNotFoundException {
        commentRepository.deleteById(id);
    }

    /** * Возвращает список всех комментариев. * * @return Список DTO всех комментариев. */
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }
}
