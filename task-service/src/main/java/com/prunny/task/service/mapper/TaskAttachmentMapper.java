package com.prunny.task.service.mapper;

import com.prunny.task.domain.Task;
import com.prunny.task.domain.TaskAttachment;
import com.prunny.task.service.dto.TaskAttachmentDTO;
import com.prunny.task.service.dto.TaskAttachmentReq;
import com.prunny.task.service.dto.TaskDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TaskAttachment} and its DTO {@link TaskAttachmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskAttachmentMapper extends EntityMapper<TaskAttachmentDTO, TaskAttachment> {
//    @Mapping(target = "task", source = "task", qualifiedByName = "taskId")
//    TaskAttachmentDTO toDto(TaskAttachment s);
//
//    @Named("taskId")
//    @BeanMapping(ignoreByDefault = true)
//    @Mapping(target = "id", source = "id")
//    TaskDTO toDtoTaskId(Task task);

    TaskAttachment toEntity(TaskAttachmentReq taskAttachmentReq);
}
