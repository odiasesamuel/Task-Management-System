package com.prunny.project.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProjectTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Project getProjectSample1() {
        return new Project().id(1L).projectName("projectName1").description("description1").teamId(1L).createdByUserId(1L);
    }

    public static Project getProjectSample2() {
        return new Project().id(2L).projectName("projectName2").description("description2").teamId(2L).createdByUserId(2L);
    }

    public static Project getProjectRandomSampleGenerator() {
        return new Project()
            .id(longCount.incrementAndGet())
            .projectName(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .teamId(longCount.incrementAndGet())
            .createdByUserId(longCount.incrementAndGet());
    }
}
