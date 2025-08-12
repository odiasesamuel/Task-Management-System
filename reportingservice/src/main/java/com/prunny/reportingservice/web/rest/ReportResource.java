package com.prunny.reportingservice.web.rest;
import com.prunny.reportingservice.service.ReportService;
import com.prunny.reportingservice.service.dto.ProjectReport;
import com.prunny.reportingservice.service.impl.ReportServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportResource {
    private final ReportServiceImpl reportService;
    public ReportResource( ReportServiceImpl reportService) {
        this.reportService = reportService;
       }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectReport>> getProjectReports() {
        return ResponseEntity.ok(reportService.getProjectReports());
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ProjectReport> getProjectReport(@PathVariable Long projectId) {
        return ResponseEntity.ok(reportService.getProjectReport(projectId));
    }

//    @GetMapping("/performance")
//    public ResponseEntity<List<UserPerformance>> getUserPerformance() {
//        return ResponseEntity.ok(service.getUserPerformance());
//    }
}

