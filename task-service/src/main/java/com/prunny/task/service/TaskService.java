package com.prunny.task.service;

import com.prunny.task.service.dto.TaskDTO;

import java.util.List;
import java.util.Optional;

import com.prunny.task.service.dto.TaskReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskDTO save(TaskReq taskReq);

    TaskDTO update(TaskDTO taskDTO);

    Optional<TaskDTO> partialUpdate(TaskDTO taskDTO);

    Page<TaskDTO> findAll(Pageable pageable);

    Optional<TaskDTO> findOne(Long id);

    List<TaskDTO> getProjectTasks(Long projectId);

    void delete(Long id);
}
