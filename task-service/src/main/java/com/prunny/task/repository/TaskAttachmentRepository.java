package com.prunny.task.repository;

import com.prunny.task.domain.TaskAttachment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TaskAttachment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {}
