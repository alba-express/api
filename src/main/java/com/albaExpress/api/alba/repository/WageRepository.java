package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Schedule;
import com.albaExpress.api.alba.entity.Wage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WageRepository extends JpaRepository<Wage, String>, WageRepositoryCustom {

    @Query("SELECT s FROM Wage s WHERE s.slave.id = :slaveId AND s.wageEndDate IS NULL")
    List<Wage> findBySlaveId(@Param("slaveId") String slaveId);
}
