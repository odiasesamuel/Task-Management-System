package com.prunny.auth_service.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuthUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AuthUser getAuthUserSample1() {
        return new AuthUser().id(1L).email("email1").password("password1");
    }

    public static AuthUser getAuthUserSample2() {
        return new AuthUser().id(2L).email("email2").password("password2");
    }

    public static AuthUser getAuthUserRandomSampleGenerator() {
        return new AuthUser().id(longCount.incrementAndGet()).email(UUID.randomUUID().toString()).password(UUID.randomUUID().toString());
    }
}
