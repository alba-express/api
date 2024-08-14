package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.ExtraSchedule;
import com.albaExpress.api.alba.entity.Slave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ExtraScheduleRepository extends JpaRepository<ExtraSchedule, String> {

    public ExtraSchedule findByExtraScheduleDateAndSlaveId(LocalDate date, String slaveId);


//    boolean existsBySlaveAndDate(Slave slave, LocalDate date);
}

