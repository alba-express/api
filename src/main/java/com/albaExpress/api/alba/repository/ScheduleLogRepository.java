package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.ScheduleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleLogRepository extends JpaRepository<ScheduleLog, String> {

    // 특정 직원의 오늘 출근 로그 조회
    Optional<ScheduleLog> findFirstBySlaveIdAndScheduleLogStartBetween(String slaveId, LocalDateTime start, LocalDateTime end);

    List<ScheduleLog> findBySlave_Id(String slaveId);
}
