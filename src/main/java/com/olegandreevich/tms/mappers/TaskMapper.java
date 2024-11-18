package com.olegandreevich.tms.mappers;

import com.olegandreevich.tms.dto.TaskDTO;
import com.olegandreevich.tms.entities.Task;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    @Autowired
    private ModelMapper modelMapper;

    public Task toEntity(TaskDTO taskDTO) {
        return modelMapper.map(taskDTO, Task.class);
    }

    public TaskDTO toDto(Task task) {
        TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);

        if (task.getAuthor() != null) {
            taskDTO.setAuthorId(task.getAuthor().getId());
        }
        if (task.getAssignee() != null) {
            taskDTO.setAssigneeId(task.getAssignee().getId());
        }

        return taskDTO;
    }
}
