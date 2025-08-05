package com.prunny.project.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProjectTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Project getProjectSample1() {
        return new Project()
            .id(1L)
            .projectName("projectName1")
            .decription("decription1")
            .teamId(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .createdByUserId(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static Project getProjectSample2() {
        return new Project()
            .id(2L)
            .projectName("projectName2")
            .decription("decription2")
            .teamId(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .createdByUserId(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static Project getProjectRandomSampleGenerator() {
        return new Project()
            .id(longCount.incrementAndGet())
            .projectName(UUID.randomUUID().toString())
            .decription(UUID.randomUUID().toString())
            .teamId(UUID.randomUUID())
            .createdByUserId(UUID.randomUUID());
    }
}
