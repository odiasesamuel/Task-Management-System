package com.prunny.task.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TaskCommentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TaskComment getTaskCommentSample1() {
        return new TaskComment().id(1L).comment("comment1").user_id(1L);
    }

    public static TaskComment getTaskCommentSample2() {
        return new TaskComment().id(2L).comment("comment2").user_id(2L);
    }

    public static TaskComment getTaskCommentRandomSampleGenerator() {
        return new TaskComment().id(longCount.incrementAndGet()).comment(UUID.randomUUID().toString()).user_id(longCount.incrementAndGet());
    }
}
