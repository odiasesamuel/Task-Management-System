package com.prunny.project.service;

import com.prunny.project.service.dto.ProgressDTO;
import com.prunny.project.service.dto.ProjectDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.prunny.project.service.dto.ProjectReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProjectService {

    ProjectDTO save(ProjectReq projectReq);


    ProjectDTO update(ProjectDTO projectDTO);


    Optional<ProjectDTO> partialUpdate(ProjectDTO projectDTO);

    Page<ProjectDTO> findAll(Pageable pageable);

    Optional<ProjectDTO> findOne(Long id);

    List<ProjectDTO> findByTeamId(Long id);

    ProgressDTO findProgress(Long id);

    void delete(Long id);

    boolean canAccessProject(Long projectId);
}
