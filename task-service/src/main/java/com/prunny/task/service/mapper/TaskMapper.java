package com.prunny.task.service.mapper;

import com.prunny.task.domain.Task;
import com.prunny.task.service.dto.TaskDTO;
import com.prunny.task.service.dto.TaskReq;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    Task toEntity(TaskReq taskReq);
}
