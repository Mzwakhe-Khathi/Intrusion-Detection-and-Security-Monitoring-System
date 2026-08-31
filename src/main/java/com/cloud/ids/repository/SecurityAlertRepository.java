package com.cloud.ids.repository;

import com.cloud.ids.entity.SecurityAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SecurityAlertRepository extends JpaRepository<SecurityAlert, Long> {
    List<SecurityAlert> findByStatus(String status);

    /**
     * Find an open alert of a specific type from a specific IP.
     * Used to prevent duplicate alerts.
     */
    @Query("SELECT a FROM SecurityAlert a " +
           "WHERE a.alertType = :alertType " +
           "AND a.sourceIp = :sourceIp " +
           "AND a.status = 'OPEN' " +
           "ORDER BY a.timestamp DESC LIMIT 1")
    Optional<SecurityAlert> findOpenAlertByTypeAndIp(
            @Param("alertType") String alertType,
            @Param("sourceIp") String sourceIp);

    /**
     * Find recent open alerts of a specific type from a specific IP within a time window.
     */
    @Query("SELECT a FROM SecurityAlert a " +
           "WHERE a.alertType = :alertType " +
           "AND a.sourceIp = :sourceIp " +
           "AND a.status = 'OPEN' " +
           "AND a.timestamp >= :since " +
           "ORDER BY a.timestamp DESC")
    List<SecurityAlert> findRecentOpenAlerts(
            @Param("alertType") String alertType,
            @Param("sourceIp") String sourceIp,
            @Param("since") LocalDateTime since);
}