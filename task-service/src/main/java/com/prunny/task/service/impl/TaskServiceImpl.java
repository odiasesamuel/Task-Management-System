package com.prunny.task.service.impl;

import com.prunny.task.client.NotificationClient;
import com.prunny.task.client.ProjectServiceClient;
import com.prunny.task.domain.Task;
import com.prunny.task.repository.TaskRepository;
import com.prunny.task.service.TaskService;
import com.prunny.task.service.dto.ProjectDTO;
import com.prunny.task.service.dto.TaskDTO;
import com.prunny.task.service.dto.TaskReq;
import com.prunny.task.service.mapper.TaskMapper;

import java.util.List;
import java.util.Optional;

import com.prunny.task.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.prunny.task.domain.Task}.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;
    private final ProjectServiceClient projectServiceClient;
    private final NotificationClient notificationClient;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper, ProjectServiceClient projectServiceClient, NotificationClient notificationClient) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.projectServiceClient = projectServiceClient;
        this.notificationClient = notificationClient;
    }

    @Override
    public TaskDTO save(TaskReq taskReq) {
        //first get the single project, then check if it exists
        LOG.debug("Request to save Task : {}", taskReq);
        Optional<ProjectDTO> project= projectServiceClient.getProject(taskReq.getProjectId());
        if (project.isEmpty()) {

            throw new BadRequestAlertException(
                "Project with ID " + taskReq.getProjectId() + " does not exist",
                "task",
                "projectNotFound"
            );
        }
        Task task = taskMapper.toEntity(taskReq);
        task = taskRepository.save(task);
        notificationClient.sendTaskAssigned(
            task.getId(),
            task.getTitle(),
            task.getAssignedToUserId(),
            task.getDueDate()
        );
        return taskMapper.toDto(task);



    }

    @Override
    public TaskDTO update(TaskDTO taskDTO) {
        LOG.debug("Request to update Task : {}", taskDTO);
        Task task = taskMapper.toEntity(taskDTO);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Override
    public Optional<TaskDTO> partialUpdate(TaskDTO taskDTO) {
        LOG.debug("Request to partially update Task : {}", taskDTO);

        return taskRepository
            .findById(taskDTO.getId())
            .map(existingTask -> {
                taskMapper.partialUpdate(existingTask, taskDTO);

                return existingTask;
            })
            .map(taskRepository::save)
            .map(taskMapper::toDto);
    }

    @Override
    public List<TaskDTO> getProjectTasks(Long projectId){
        List<Task> tasks= taskRepository.findByProjectId(projectId);
        return tasks.stream().map(taskMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Tasks");
        return taskRepository.findAll(pageable).map(taskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskDTO> findOne(Long id) {
        LOG.debug("Request to get Task : {}", id);
        return taskRepository.findById(id).map(taskMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Task : {}", id);
        taskRepository.deleteById(id);
    }
}
