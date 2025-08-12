package com.prunny.task.service;

import com.prunny.task.service.dto.TaskAttachmentDTO;
import java.util.Optional;

import com.prunny.task.service.dto.TaskAttachmentReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


public interface TaskAttachmentService {

    TaskAttachmentDTO save(Long taskId, MultipartFile file);

    TaskAttachmentDTO update(TaskAttachmentDTO taskAttachmentDTO);

    Optional<TaskAttachmentDTO> partialUpdate(TaskAttachmentDTO taskAttachmentDTO);

    Page<TaskAttachmentDTO> findAll(Pageable pageable);


    Optional<TaskAttachmentDTO> findOne(Long id);

    void delete(Long id);
}
