package com.prunny.reportingservice.service.impl;

import com.prunny.reportingservice.client.ProjectServiceClient;
import com.prunny.reportingservice.client.TaskServiceClient;
import com.prunny.reportingservice.client.UserServiceClient;
import com.prunny.reportingservice.service.ReportService;
import com.prunny.reportingservice.service.dto.ProjectDTO;
import com.prunny.reportingservice.service.dto.ProjectReport;
import com.prunny.reportingservice.service.dto.TaskDTO;
import com.prunny.reportingservice.service.dto.UserDTO;
import com.prunny.reportingservice.service.dto.enumeration.TaskStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    private  final TaskServiceClient taskServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;

    public ReportServiceImpl(TaskServiceClient taskServiceClient, ProjectServiceClient projectServiceClient, UserServiceClient userServiceClient) {
        this.taskServiceClient = taskServiceClient;
        this.projectServiceClient = projectServiceClient;
        this.userServiceClient = userServiceClient;
    }
    private int computePercent(int completed, int total) {
        if (total == 0) return 0;
        float res = (completed / (float) total) * 100.0f;
        return Math.round(res);
    }

    @Override
    public List<ProjectReport> getProjectReports() {
        List<ProjectDTO> projects = projectServiceClient.getAllProjects();
        return projects.stream()
            .map(project -> {
                List<TaskDTO> tasks = taskServiceClient.getAllTasks(project.getId());
                int total = tasks.size();
//                int completed = (int) tasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
                int completed = (int) tasks.stream().filter(t -> TaskStatus.COMPLETED==(t.getStatus())).count();
                int progress = computePercent(completed, total);
                return new ProjectReport(project.getId(), project.getProjectName(), total, completed, progress);
            })
            .collect(Collectors.toList());
    }

    @Override
    public ProjectReport getProjectReport(Long projectId) {
        // find project (from list or call single endpoint)
        // assume project exists
        List<TaskDTO> tasks = taskServiceClient.getAllTasks(projectId);
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(t -> TaskStatus.COMPLETED==t.getStatus()).count();
        int progress = computePercent(completed, total);
        // you may need project name — call projectClient
        ProjectDTO project = projectServiceClient.getAllProjects()
            .stream().filter(p -> projectId.equals(p.getId())).findFirst().orElse(null);
        String name = project != null ? project.getProjectName() : "unknown";
        return new ProjectReport(projectId, name, total, completed, progress);
    }

//    public List<UserPerformance> getUserPerformance() {
//        List<UserDTO> users = userServiceClient.getAllUsers();
//        return users.stream()
//            .map(u -> {
//                List<TaskDTO> tasks = taskServiceClient.getTasksByUserId(u.getId());
//                int completed = (int) tasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus())).count();
//                int total = tasks.size();
//                int completionRate = computePercent(completed, total);
//                return new UserPerformance(u.getId(), u.getName(), total, completed, completionRate);
//            })
//            .collect(Collectors.toList());
//    }
}
