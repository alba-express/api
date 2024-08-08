package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Schedule;
import com.albaExpress.api.alba.entity.Slave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, String>, ScheduleRepositoryCustom {

    @Query("SELECT s FROM Schedule s WHERE s.scheduleDay = :day")
    List<Schedule> findByScheduleDay(@Param("day") int day);

    @Query("SELECT s FROM Schedule s WHERE s.slave.id = :slaveId AND s.scheduleDay = :day")
    List<Schedule> findBySlaveIdAndScheduleDay(@Param("slaveId") String slaveId, @Param("day") int day);

}
