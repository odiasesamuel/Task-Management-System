package com.prunny.task.web.rest;

import com.prunny.task.repository.TaskRepository;
import com.prunny.task.service.TaskService;
import com.prunny.task.service.dto.TaskDTO;
import com.prunny.task.service.dto.TaskReq;
import com.prunny.task.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;


@RestController
@RequestMapping("/api/tasks")
public class TaskResource {

    private static final Logger LOG = LoggerFactory.getLogger(TaskResource.class);

    private static final String ENTITY_NAME = "taskServiceTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskService taskService;

    private final TaskRepository taskRepository;

    public TaskResource(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @PostMapping("")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskReq taskReq) throws URISyntaxException {
        LOG.debug("REST request to save Task : {}", taskReq);
        if (taskReq.getId() != null) {
            throw new BadRequestAlertException("A new task cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaskDTO taskDTO = taskService.save(taskReq);
        return ResponseEntity.created(new URI("/api/tasks/" + taskDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, taskDTO.getId().toString()))
            .body(taskDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TaskDTO taskDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Task : {}, {}", id, taskDTO);
        if (taskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        taskDTO = taskService.update(taskDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskDTO.getId().toString()))
            .body(taskDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskDTO> partialUpdateTask(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TaskDTO taskDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Task partially : {}, {}", id, taskDTO);
        if (taskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskDTO> result = taskService.partialUpdate(taskDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskDTO.getId().toString())
        );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @GetMapping("")
    public ResponseEntity<List<TaskDTO>> getAllTasks(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Tasks");
        Page<TaskDTO> page = taskService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD') or @taskServiceImpl.canAccessProject(#projectId)")
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Task : {}", id);
        Optional<TaskDTO> taskDTO = taskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD') or @taskServiceImpl.canAccessProject(#projectId)")
    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<TaskDTO>> getProjectTask(@PathVariable("projectId") Long projectId) {
        LOG.debug("REST request to get Task : {}", projectId);
        List<TaskDTO> taskDTO = taskService.getProjectTasks(projectId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(taskDTO));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Task : {}", id);
        taskService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
