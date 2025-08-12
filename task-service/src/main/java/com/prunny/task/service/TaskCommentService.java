package com.prunny.task.service;

import com.prunny.task.service.dto.TaskCommentDTO;

import java.util.List;
import java.util.Optional;

import com.prunny.task.service.dto.TaskCommentReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.prunny.task.domain.TaskComment}.
 */
public interface TaskCommentService {

    TaskCommentDTO save(TaskCommentReq taskCommentReq, Long taskId);

    TaskCommentDTO update(TaskCommentDTO taskCommentDTO);

    Optional<TaskCommentDTO> partialUpdate(TaskCommentDTO taskCommentDTO);

    Page<TaskCommentDTO> findAll(Pageable pageable);

    List<TaskCommentDTO> findTaskComments(Long id);

    List<TaskCommentDTO> getTaskComments(Long taskId);

    void delete(Long id);
}
