package com.prunny.task.service.impl;

import com.prunny.task.domain.Task;
import com.prunny.task.domain.TaskAttachment;
import com.prunny.task.repository.TaskAttachmentRepository;
import com.prunny.task.service.TaskAttachmentService;
import com.prunny.task.service.dto.TaskAttachmentDTO;
import com.prunny.task.service.dto.TaskAttachmentReq;
import com.prunny.task.service.mapper.TaskAttachmentMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link com.prunny.task.domain.TaskAttachment}.
 */
@Service
@Transactional
public class TaskAttachmentServiceImpl implements TaskAttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskAttachmentServiceImpl.class);

    private final TaskAttachmentRepository taskAttachmentRepository;

    private final TaskAttachmentMapper taskAttachmentMapper;

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    public TaskAttachmentServiceImpl(TaskAttachmentRepository taskAttachmentRepository, TaskAttachmentMapper taskAttachmentMapper) {
        this.taskAttachmentRepository = taskAttachmentRepository;
        this.taskAttachmentMapper = taskAttachmentMapper;
    }


//    public TaskAttachmentDTO save(TaskAttachmentReq taskAttachmentReq, Long taskId) {
//        LOG.debug("Request to save TaskAttachment : {}", taskAttachmentReq);
//        Task task = new Task();
//        task.setId(taskId);
//        TaskAttachment taskAttachment = taskAttachmentMapper.toEntity(taskAttachmentReq);
//        taskAttachment.setTask(task);
//        taskAttachment = taskAttachmentRepository.save(taskAttachment);
//        return taskAttachmentMapper.toDto(taskAttachment);
//    }
@Override
    public TaskAttachmentDTO save(Long taskId, MultipartFile file) {
        try {
            // Create upload directory if not exists
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // Save file to local storage
            String originalFileName = file.getOriginalFilename();
            String storedFileName = UUID.randomUUID() + "_" + originalFileName;
            Path filePath = dirPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Build file URL (mock for now — local server path)
            String fileUrl = "/uploads/" + storedFileName;

            // Get logged-in user ID (mock for now)
            Long uploadedBy = getCurrentUserId();
        Task task = new Task();
        task.setId(taskId);
            // Save attachment entity
            TaskAttachment attachment = new TaskAttachment();
            attachment.setId(null);
            attachment.setFileName(originalFileName);
            attachment.setFileUrl(fileUrl);
            attachment.setUploadedByUserId(uploadedBy);
            attachment.setTask(task);

            taskAttachmentRepository.save(attachment);
            return taskAttachmentMapper.toDto(attachment);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    private Long getCurrentUserId() {
        // Mock until user service ready
        return 1L; // replace with actual user from SecurityContext
    }

//    private Long getCurrentUserId() {
//        var auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth instanceof JwtAuthenticationToken jwtAuth) {
//            return Long.valueOf(jwtAuth.getToken().getClaim("user_id"));
//        }
//        return null;
//    }



    @Override
    public TaskAttachmentDTO update(TaskAttachmentDTO taskAttachmentDTO) {
        LOG.debug("Request to update TaskAttachment : {}", taskAttachmentDTO);
        TaskAttachment taskAttachment = taskAttachmentMapper.toEntity(taskAttachmentDTO);
        taskAttachment = taskAttachmentRepository.save(taskAttachment);
        return taskAttachmentMapper.toDto(taskAttachment);
    }

    @Override
    public Optional<TaskAttachmentDTO> partialUpdate(TaskAttachmentDTO taskAttachmentDTO) {
        LOG.debug("Request to partially update TaskAttachment : {}", taskAttachmentDTO);

        return taskAttachmentRepository
            .findById(taskAttachmentDTO.getId())
            .map(existingTaskAttachment -> {
                taskAttachmentMapper.partialUpdate(existingTaskAttachment, taskAttachmentDTO);

                return existingTaskAttachment;
            })
            .map(taskAttachmentRepository::save)
            .map(taskAttachmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskAttachmentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TaskAttachments");
        return taskAttachmentRepository.findAll(pageable).map(taskAttachmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TaskAttachmentDTO> findOne(Long id) {
        LOG.debug("Request to get TaskAttachment : {}", id);
        return taskAttachmentRepository.findById(id).map(taskAttachmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TaskAttachment : {}", id);
        taskAttachmentRepository.deleteById(id);
    }
}
