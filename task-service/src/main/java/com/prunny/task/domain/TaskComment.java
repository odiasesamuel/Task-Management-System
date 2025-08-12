package com.prunny.task.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TaskComment.
 */
@Entity
@Table(name = "task_comment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaskComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 1000)
    @Column(name = "comment", length = 1000, nullable = false)
    private String comment;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long user_id;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    @PrePersist
    public void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public TaskComment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return this.comment;
    }

    public TaskComment comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getUser_id() {
        return this.user_id;
    }

    public TaskComment user_id(Long user_id) {
        this.setUser_id(user_id);
        return this;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public TaskComment createdAt(ZonedDateTime createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskComment task(Task task) {
        this.setTask(task);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskComment)) {
            return false;
        }
        return getId() != null && getId().equals(((TaskComment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskComment{" +
            "id=" + getId() +
            ", comment='" + getComment() + "'" +
            ", user_id=" + getUser_id() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
