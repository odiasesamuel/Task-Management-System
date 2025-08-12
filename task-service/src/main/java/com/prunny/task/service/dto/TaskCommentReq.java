package com.prunny.task.service.dto;



import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaskCommentReq implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 1000)
    private String comment;

    @NotNull
    private Long user_id;
    // Get this user Id from spring security context and not body of the request



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskCommentReq taskCommentDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taskCommentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskCommentDTO{" +
            "id=" + getId() +
            ", comment='" + getComment() + "'" +
            ", user_id=" + getUser_id() +
            "}";
    }
}

