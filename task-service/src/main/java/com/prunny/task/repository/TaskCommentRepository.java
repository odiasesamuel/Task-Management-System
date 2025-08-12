package com.prunny.task.repository;

import com.prunny.task.domain.TaskComment;
import com.prunny.task.service.dto.TaskCommentDTO;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the TaskComment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    List<TaskComment> findByTaskId(Long taskId);
}
