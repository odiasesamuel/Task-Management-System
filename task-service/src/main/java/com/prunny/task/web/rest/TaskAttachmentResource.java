package com.prunny.task.web.rest;

import com.prunny.task.repository.TaskAttachmentRepository;
import com.prunny.task.service.TaskAttachmentService;
import com.prunny.task.service.dto.TaskAttachmentDTO;
import com.prunny.task.service.dto.TaskAttachmentReq;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;


@RestController
@RequestMapping("/api/task-attachments")
public class TaskAttachmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(TaskAttachmentResource.class);

    private static final String ENTITY_NAME = "taskServiceTaskAttachment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskAttachmentService taskAttachmentService;

    private final TaskAttachmentRepository taskAttachmentRepository;

    public TaskAttachmentResource(TaskAttachmentService taskAttachmentService, TaskAttachmentRepository taskAttachmentRepository) {
        this.taskAttachmentService = taskAttachmentService;
        this.taskAttachmentRepository = taskAttachmentRepository;
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD') or @taskAttachmentServiceImpl.canAccessUploadTaskAttachment(#taskId)")
    @PostMapping("/{taskId}/attachments")
    public ResponseEntity<TaskAttachmentDTO> uploadAttachment(
        @PathVariable Long taskId,
        @RequestParam("file") MultipartFile file) {

        TaskAttachmentDTO result = taskAttachmentService.save(taskId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskAttachmentDTO> updateTaskAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TaskAttachmentDTO taskAttachmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TaskAttachment : {}, {}", id, taskAttachmentDTO);
        if (taskAttachmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskAttachmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        taskAttachmentDTO = taskAttachmentService.update(taskAttachmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskAttachmentDTO.getId().toString()))
            .body(taskAttachmentDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskAttachmentDTO> partialUpdateTaskAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TaskAttachmentDTO taskAttachmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TaskAttachment partially : {}, {}", id, taskAttachmentDTO);
        if (taskAttachmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskAttachmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskAttachmentDTO> result = taskAttachmentService.partialUpdate(taskAttachmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskAttachmentDTO.getId().toString())
        );
    }


    @GetMapping("")
    public ResponseEntity<List<TaskAttachmentDTO>> getAllTaskAttachments(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of TaskAttachments");
        Page<TaskAttachmentDTO> page = taskAttachmentService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskAttachmentDTO> getTaskAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TaskAttachment : {}", id);
        Optional<TaskAttachmentDTO> taskAttachmentDTO = taskAttachmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskAttachmentDTO);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEAM_LEAD')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TaskAttachment : {}", id);
        taskAttachmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
