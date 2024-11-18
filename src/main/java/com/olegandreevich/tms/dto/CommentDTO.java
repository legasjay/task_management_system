package com.olegandreevich.tms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private String content;
//    private LocalDateTime createdAt; // can be omitted if not needed in the response
}
