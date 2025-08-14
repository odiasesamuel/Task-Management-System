package com.prunny.task.service.impl;

import com.prunny.task.client.NotificationClient;
import com.prunny.task.domain.Task;
import com.prunny.task.domain.TaskComment;
import com.prunny.task.repository.TaskCommentRepository;
import com.prunny.task.service.TaskCommentService;
import com.prunny.task.service.dto.TaskCommentDTO;
import com.prunny.task.service.dto.TaskCommentReq;
import com.prunny.task.service.mapper.TaskCommentMapper;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.prunny.task.domain.TaskComment}.
 */
@Service
@Transactional
public class TaskCommentServiceImpl implements TaskCommentService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskCommentServiceImpl.class);

    private final TaskCommentRepository taskCommentRepository;

    private final TaskCommentMapper taskCommentMapper;
    private final NotificationClient notificationClient;

    public TaskCommentServiceImpl(TaskCommentRepository taskCommentRepository, TaskCommentMapper taskCommentMapper, NotificationClient notificationClient) {
        this.taskCommentRepository = taskCommentRepository;
        this.taskCommentMapper = taskCommentMapper;
        this.notificationClient = notificationClient;
    }

    @Override
    public TaskCommentDTO save(TaskCommentReq taskCommentReq,Long taskId) {
        LOG.debug("Request to save TaskComment : {}", taskCommentReq);
        Task task = new Task();
        task.setId(taskId);
        TaskComment taskComment = taskCommentMapper.toEntity(taskCommentReq);
        taskComment.setTask(task);
        taskComment = taskCommentRepository.save(taskComment);
        notificationClient.sendTaskCommented(
            taskComment.getTask().getId(),
            taskComment.getUser_id(),
            taskCommentReq.getComment(),
            ZonedDateTime.now()
        );

        return taskCommentMapper.toDto(taskComment);
    }

    @Override
    public TaskCommentDTO update(TaskCommentDTO taskCommentDTO) {
        LOG.debug("Request to update TaskComment : {}", taskCommentDTO);
        TaskComment taskComment = taskCommentMapper.toEntity(taskCommentDTO);
        taskComment = taskCommentRepository.save(taskComment);
        return taskCommentMapper.toDto(taskComment);
    }

    @Override
    public Optional<TaskCommentDTO> partialUpdate(TaskCommentDTO taskCommentDTO) {
        LOG.debug("Request to partially update TaskComment : {}", taskCommentDTO);

        return taskCommentRepository
            .findById(taskCommentDTO.getId())
            .map(existingTaskComment -> {
                taskCommentMapper.partialUpdate(existingTaskComment, taskCommentDTO);

                return existingTaskComment;
            })
            .map(taskCommentRepository::save)
            .map(taskCommentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskCommentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TaskComments");
        return taskCommentRepository.findAll(pageable).map(taskCommentMapper::toDto);
    }

    //Find comment by task id
    @Override
    @Transactional(readOnly = true)
    public List<TaskCommentDTO> findTaskComments(Long taskId) {
        LOG.debug("Request to get TaskComments for Task : {}", taskId);
        List<TaskComment> taskComments = taskCommentRepository.findByTaskId(taskId);
        return taskComments.stream()
            .map(taskCommentMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<TaskCommentDTO> getTaskComments(Long taskId){
        List<TaskComment> tasks = taskCommentRepository.findByTaskId(taskId);
        return tasks.stream().map(taskCommentMapper::toDto).toList();
    }


    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TaskComment : {}", id);
        taskCommentRepository.deleteById(id);
    }
}
