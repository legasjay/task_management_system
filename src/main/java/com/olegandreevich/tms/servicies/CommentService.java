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

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ModelMapper modelMapper;

    public CommentDTO addComment(Long taskId, Long userId, CommentDTO commentDTO) throws ResourceNotFoundException {
        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setTask(new Task(taskId)); // Assume you have a method to fetch Task by ID
        comment.setUser(new User(userId));
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsForTask(Long taskId) {
        return commentRepository.findAll()
                .stream()
                .filter(comment -> comment.getTask().getId().equals(taskId))
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long id) throws ResourceNotFoundException {
        commentRepository.deleteById(id);
    }

    public List<CommentDTO> findAll() {
        return commentRepository.findAll()
                .stream()
                .map((element) -> modelMapper.map(element, CommentDTO.class))
                .toList();
    }
}
