package com.cloud.ids.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class BruteForceLockoutService {

    private static final long LOCKOUT_MINUTES = 2;
    private final ConcurrentMap<String, LocalDateTime> lockouts = new ConcurrentHashMap<>();

    public void lockout(String ipAddress) {
        lockouts.put(ipAddress, LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
    }

    public boolean isLocked(String ipAddress) {
        LocalDateTime lockedUntil = lockouts.get(ipAddress);
        if (lockedUntil == null) {
            return false;
        }

        if (!LocalDateTime.now().isBefore(lockedUntil)) {
            lockouts.remove(ipAddress, lockedUntil);
            return false;
        }

        return true;
    }
}