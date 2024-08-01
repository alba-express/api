package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.ScheduleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleLogRepository extends JpaRepository<ScheduleLog, String> {
}
