package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {

    // 특정 요일에 대한 스케줄을 조회하는 쿼리
    @Query("SELECT s FROM Schedule s WHERE s.scheduleDay = :day")
    List<Schedule> findByScheduleDay(@Param("day") int day);

    // 특정 직원 ID와 요일에 대한 스케줄을 조회하는 쿼리
    @Query("SELECT s FROM Schedule s WHERE s.slave.id = :slaveId AND s.scheduleDay = :day")
    List<Schedule> findBySlaveIdAndScheduleDay(@Param("slaveId") String slaveId, @Param("day") int day);
}
