package com.prunny.reportingservice.service.dto;


import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectReport implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2)
    private String projectName;


    private int total;

    @NotNull
    private int completed;

    @NotNull
    private int progress;


    public ProjectReport(Long id, String projectName, int total, int completed, int progress) {
        this.id=id;
        this.projectName=projectName;
        this.total=total;
        this.completed=completed;
        this.progress=progress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectReport projectReport)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectReport.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectReport{" +
            "id=" + getId() +
            ", projectName='" + getProjectName() + "'" +
            ", total='" + getTotal() + "'" +
            ", completed=" + getCompleted() +
            ", progress=" + getProgress() +
            "}";
    }
}

