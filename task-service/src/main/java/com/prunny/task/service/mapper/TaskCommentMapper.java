package com.prunny.task.service.mapper;

import com.prunny.task.domain.Task;
import com.prunny.task.domain.TaskComment;
import com.prunny.task.service.dto.TaskCommentDTO;
import com.prunny.task.service.dto.TaskCommentReq;
import com.prunny.task.service.dto.TaskDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TaskComment} and its DTO {@link TaskCommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskCommentMapper extends EntityMapper<TaskCommentDTO, TaskComment> {
//    @Mapping(target = "task", source = "task", qualifiedByName = "taskId")
//    TaskCommentDTO toDto(TaskComment s);
//
//    @Named("taskId")
//    @BeanMapping(ignoreByDefault = true)
//    @Mapping(target = "id", source = "id")
//    TaskDTO toDtoTaskId(Task task);

    TaskComment toEntity(TaskCommentReq taskCommentReq);
}
