package com.prunny.project.service.dto;



import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.prunny.project.domain.Project} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProgressDTO implements Serializable {

//    private Long id;

    @NotNull
    @Size(min = 2)
    private String projectName;

    @NotNull
    private float progress;



//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProgressDTO progressDTO)) {
            return false;
        }

//        if (this.id == null) {
//            return false;
//        }
//        return Objects.equals(this.id, progressDTO.id);
        return false;
    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(this.id);
//    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectDTO{" +
//            "id=" + getId() +
            ", projectName='" + getProjectName() + "'" +
            ", progress='" + getProgress() + "'" +
            "}";
    }
}
