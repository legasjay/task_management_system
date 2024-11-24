package com.olegandreevich.tms.dto;

import com.olegandreevich.tms.entities.enums.Priority;
import com.olegandreevich.tms.entities.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskDTO {
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private Long authorId;
    private Long assigneeId;
}
