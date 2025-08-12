package com.prunny.task.service.dto;


import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.prunny.task.domain.TaskAttachment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaskAttachmentReq implements Serializable {

    private Long id;

    @NotNull
    private String fileName;

    @NotNull
    private String fileUrl;

    private Long uploadedByUserId;


//    private TaskDTO task;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getUploadedByUserId() {
        return uploadedByUserId;
    }

    public void setUploadedByUserId(Long uploadedByUserId) {
        this.uploadedByUserId = uploadedByUserId;
    }


//    public TaskDTO getTask() {
//        return task;
//    }
//
//    public void setTask(TaskDTO task) {
//        this.task = task;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskAttachmentReq taskAttachmentDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taskAttachmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskAttachmentDTO{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", fileUrl='" + getFileUrl() + "'" +
            ", uploadedByUserId=" + getUploadedByUserId() +
//            ", task=" + getTask() +
            "}";
    }
}
