package com.olegandreevich.tms.mappers;

import com.olegandreevich.tms.dto.TaskDTO;
import com.olegandreevich.tms.dto.TaskDTOGet;
import com.olegandreevich.tms.entities.Task;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskMapperGet {

    @Autowired
    private ModelMapper modelMapper;

    public Task toEntity(TaskDTOGet taskDTOGet) {
        return modelMapper.map(taskDTOGet, Task.class);
    }

    public TaskDTOGet toDto(Task task) {
        TaskDTOGet taskDTOGet = modelMapper.map(task, TaskDTOGet.class);

        if (task.getAuthor() != null) {
            taskDTOGet.setAuthorId(task.getAuthor().getId());
        }
        if (task.getAssignee() != null) {
            taskDTOGet.setAssigneeId(task.getAssignee().getId());
        }

        return taskDTOGet;
    }
}
