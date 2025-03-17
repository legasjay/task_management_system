package com.olegandreevich.tms.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskWithCommentsDTO extends TaskDTOGet {
    private List<CommentDTO> comments;
}
