package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Schedule;
import com.albaExpress.api.alba.entity.Slave;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, String>, ScheduleRepositoryCustom {


    // 특정 요일에 대한 스케줄을 조회하는 쿼리
//    @Query("SELECT s FROM Schedule s WHERE s.scheduleDay = :day AND s.workplace.id = :workplaceId")
//    List<Schedule> findByScheduleDay(@Param("day") int day, @Param("workplaceId") String workplaceId);

    // 특정 직원 ID와 요일에 대한 스케줄을 조회하는 쿼리
    @Query("SELECT s FROM Schedule s WHERE s.slave.id = :slaveId AND s.scheduleDay = :day")
    List<Schedule> findBySlaveIdAndScheduleDay(@Param("slaveId") String slaveId, @Param("day") int day);

    // slaveId를 통해 Schedule 목록을 찾는 쿼리 메서드
    @Query("SELECT s FROM Schedule s WHERE s.slave.id = :slaveId")
    List<Schedule> findBySlaveId(@Param("slaveId") String slaveId);

}
