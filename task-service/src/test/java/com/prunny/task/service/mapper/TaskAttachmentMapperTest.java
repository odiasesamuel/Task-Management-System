package com.prunny.task.service.mapper;

import static com.prunny.task.domain.TaskAttachmentAsserts.*;
import static com.prunny.task.domain.TaskAttachmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskAttachmentMapperTest {

    private TaskAttachmentMapper taskAttachmentMapper;

    @BeforeEach
    void setUp() {
        taskAttachmentMapper = new TaskAttachmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTaskAttachmentSample1();
        var actual = taskAttachmentMapper.toEntity(taskAttachmentMapper.toDto(expected));
        assertTaskAttachmentAllPropertiesEquals(expected, actual);
    }
}
