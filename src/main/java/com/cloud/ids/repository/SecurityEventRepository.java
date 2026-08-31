package com.cloud.ids.repository;

import com.cloud.ids.entity.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SecurityEventRepository
        extends JpaRepository<SecurityEvent, Long> {

    /**
     * Find all failed login attempts from a specific IP within a time window.
     * Used for brute-force detection.
     */
    @Query("SELECT e FROM SecurityEvent e " +
           "WHERE e.eventType = 'LOGIN_FAILURE' " +
           "AND e.sourceIp = :sourceIp " +
           "AND e.timestamp >= :since " +
           "AND e.timestamp <= :until " +
           "ORDER BY e.timestamp DESC")
    List<SecurityEvent> findFailedLoginsByIpAndTime(
            @Param("sourceIp") String sourceIp,
            @Param("since") LocalDateTime since,
            @Param("until") LocalDateTime until);

    /**
     * Count failed login attempts from a specific IP within a time window.
     */
    @Query("SELECT COUNT(e) FROM SecurityEvent e " +
           "WHERE e.eventType = 'LOGIN_FAILURE' " +
           "AND e.sourceIp = :sourceIp " +
           "AND e.timestamp >= :since " +
           "AND e.timestamp <= :until")
    long countFailedLoginsByIpAndTime(
            @Param("sourceIp") String sourceIp,
            @Param("since") LocalDateTime since,
            @Param("until") LocalDateTime until);

    /**
     * Find all events from a specific IP.
     */
    @Query("SELECT e FROM SecurityEvent e WHERE e.sourceIp = :sourceIp ORDER BY e.timestamp DESC")
    List<SecurityEvent> findBySourceIp(@Param("sourceIp") String sourceIp);
}