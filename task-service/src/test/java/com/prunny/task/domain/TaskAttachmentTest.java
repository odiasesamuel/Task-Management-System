package com.prunny.task.domain;

import static com.prunny.task.domain.TaskAttachmentTestSamples.*;
import static com.prunny.task.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.prunny.task.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskAttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskAttachment.class);
        TaskAttachment taskAttachment1 = getTaskAttachmentSample1();
        TaskAttachment taskAttachment2 = new TaskAttachment();
        assertThat(taskAttachment1).isNotEqualTo(taskAttachment2);

        taskAttachment2.setId(taskAttachment1.getId());
        assertThat(taskAttachment1).isEqualTo(taskAttachment2);

        taskAttachment2 = getTaskAttachmentSample2();
        assertThat(taskAttachment1).isNotEqualTo(taskAttachment2);
    }

    @Test
    void taskTest() {
        TaskAttachment taskAttachment = getTaskAttachmentRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        taskAttachment.setTask(taskBack);
        assertThat(taskAttachment.getTask()).isEqualTo(taskBack);

        taskAttachment.task(null);
        assertThat(taskAttachment.getTask()).isNull();
    }
}
