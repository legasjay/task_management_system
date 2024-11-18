package com.olegandreevich.tms.mappers;

import com.olegandreevich.tms.dto.CommentDTO;
import com.olegandreevich.tms.entities.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Comment toEntity(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }

    public CommentDTO toDto(Comment comment) {
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);


        return commentDTO;
    }
}
