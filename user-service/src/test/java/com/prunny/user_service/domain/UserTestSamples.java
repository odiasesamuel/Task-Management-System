package com.prunny.user_service.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static User getUserSample1() {
        return new User().id(1L).name("name1").email("email1").phoneNumber("phoneNumber1").profilePictureUrl("profilePictureUrl1");
    }

    public static User getUserSample2() {
        return new User().id(2L).name("name2").email("email2").phoneNumber("phoneNumber2").profilePictureUrl("profilePictureUrl2");
    }

    public static User getUserRandomSampleGenerator() {
        return new User()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .profilePictureUrl(UUID.randomUUID().toString());
    }
}
