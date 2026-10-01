package com.cloud.ids.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BruteForceLockoutServiceTest {

    private final BruteForceLockoutService service = new BruteForceLockoutService();

    @Test
    void lockoutAppliesOnlyToTheTriggeredIp() {
        service.lockout("192.0.2.10");

        assertThat(service.isLocked("192.0.2.10")).isTrue();
        assertThat(service.isLocked("192.0.2.11")).isFalse();
    }
}