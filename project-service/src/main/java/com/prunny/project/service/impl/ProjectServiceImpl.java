package com.prunny.project.service.impl;

import com.prunny.project.client.TaskServiceClient;
import com.prunny.project.domain.Project;
import com.prunny.project.repository.ProjectRepository;
import com.prunny.project.service.ProjectService;
import com.prunny.project.service.dto.ProgressDTO;
import com.prunny.project.service.dto.ProjectDTO;
import com.prunny.project.service.dto.ProjectReq;
import com.prunny.project.service.dto.TaskDTO;
import com.prunny.project.service.dto.enums.TaskStatus;
import com.prunny.project.service.mapper.ProjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final TaskServiceClient taskServiceClient;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper, TaskServiceClient taskServiceClient) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.taskServiceClient = taskServiceClient;
    }

    @Override
    public ProjectDTO save(ProjectReq projectReq) {
        LOG.debug("Request to save Project : {}", projectReq);
        Project project = projectMapper.toEntity(projectReq);
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    public ProjectDTO update(ProjectDTO projectDTO) {
        LOG.debug("Request to update Project : {}", projectDTO);
        Project project = projectMapper.toEntity(projectDTO);
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    public Optional<ProjectDTO> partialUpdate(ProjectDTO projectDTO) {
        LOG.debug("Request to partially update Project : {}", projectDTO);

        return projectRepository
            .findById(projectDTO.getId())
            .map(existingProject -> {
                projectMapper.partialUpdate(existingProject, projectDTO);

                return existingProject;
            })
            .map(projectRepository::save)
            .map(projectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Projects");
        return projectRepository.findAll(pageable).map(projectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectDTO> findOne(Long id) {
        LOG.debug("Request to get Project : {}", id);
        return projectRepository.findById(id).map(projectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> findByTeamId(Long teamId) {
        LOG.debug("Request to get Project : {}", teamId);
        List<Project> projects= projectRepository.findByTeamId(teamId);
        return projects.stream().map(projectMapper::toDto).toList();

    }

    @Override
    @Transactional(readOnly = true)
    public ProgressDTO findProgress(Long id) {
        LOG.debug("Request to get Project : {}", id);
        Optional<ProjectDTO> project= findOne(id);

        List<TaskDTO> tasks = taskServiceClient.getAllTasks(id);
        List<TaskDTO> filteredTasks = tasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
            .toList();
        int total = tasks.size();
        int completed = filteredTasks.size();
        float res = (total == 0) ? 0f : (completed / (float) total);
//        float res= filteredTasks.toArray().length/(float) tasks.toArray().length ;


        ProgressDTO progressDTO= new ProgressDTO();
        project.ifPresent(ans -> progressDTO.setProjectName(ans.getProjectName()));

        progressDTO.setProgress(res);

        return  progressDTO;


    }


    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Project : {}", id);
        projectRepository.deleteById(id);
    }
}
