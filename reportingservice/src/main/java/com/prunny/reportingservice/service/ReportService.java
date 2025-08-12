package com.prunny.reportingservice.service;

import com.prunny.reportingservice.service.dto.ProjectReport;

import java.util.List;

public interface ReportService {
List<ProjectReport> getProjectReports();
ProjectReport getProjectReport(Long projectId);
}
