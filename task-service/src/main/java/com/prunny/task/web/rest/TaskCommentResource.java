package com.prunny.task.web.rest;

import com.prunny.task.domain.Task;
import com.prunny.task.repository.TaskCommentRepository;
import com.prunny.task.service.TaskCommentService;
import com.prunny.task.service.dto.TaskCommentDTO;
import com.prunny.task.service.dto.TaskCommentReq;
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
@RequestMapping("/api/task-comments")
public class TaskCommentResource {

    private static final Logger LOG = LoggerFactory.getLogger(TaskCommentResource.class);

    private static final String ENTITY_NAME = "taskServiceTaskComment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskCommentService taskCommentService;

    private final TaskCommentRepository taskCommentRepository;

    public TaskCommentResource(TaskCommentService taskCommentService, TaskCommentRepository taskCommentRepository) {
        this.taskCommentService = taskCommentService;
        this.taskCommentRepository = taskCommentRepository;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @PostMapping("/{taskId}")
    public ResponseEntity<TaskCommentDTO> createTaskComment( @PathVariable(value = "taskId", required = false) final Long taskId,@Valid @RequestBody TaskCommentReq taskCommentReq) throws URISyntaxException {
        LOG.debug("REST request to save TaskComment : {}", taskCommentReq);
        if (taskCommentReq.getId() != null) {
            throw new BadRequestAlertException("A new taskComment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaskCommentDTO taskCommentDTO = taskCommentService.save(taskCommentReq,taskId);
        return ResponseEntity.created(new URI("/api/task-comments/" + taskCommentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, taskCommentDTO.getId().toString()))
            .body(taskCommentDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskCommentDTO> updateTaskComment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TaskCommentDTO taskCommentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TaskComment : {}, {}", id, taskCommentDTO);
        if (taskCommentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskCommentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskCommentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        taskCommentDTO = taskCommentService.update(taskCommentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskCommentDTO.getId().toString()))
            .body(taskCommentDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskCommentDTO> partialUpdateTaskComment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TaskCommentDTO taskCommentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TaskComment partially : {}, {}", id, taskCommentDTO);
        if (taskCommentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskCommentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskCommentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskCommentDTO> result = taskCommentService.partialUpdate(taskCommentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskCommentDTO.getId().toString())
        );
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @GetMapping("")
    public ResponseEntity<List<TaskCommentDTO>> getAllTaskComments(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of TaskComments");
        Page<TaskCommentDTO> page = taskCommentService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<TaskCommentDTO> getTaskComment(@PathVariable("id") Long id) {
//        LOG.debug("REST request to get TaskComment : {}", id);
//        Optional<TaskCommentDTO> taskCommentDTO = taskCommentService.findOne(id);
//        return ResponseUtil.wrapOrNotFound(taskCommentDTO);
//    }

    //get all comments by taskId
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD') or @taskCommentServiceImpl.canAccessTaskComment(#taskId)")
    @GetMapping("/{taskId}")
    public ResponseEntity<List<TaskCommentDTO>> getTaskComment(@PathVariable("taskId") Long taskId) {
        LOG.debug("REST request to get TaskComment : {}", taskId);
        List<TaskCommentDTO> taskCommentDTO = taskCommentService.findTaskComments(taskId);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(taskCommentDTO));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskComment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TaskComment : {}", id);
        taskCommentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
