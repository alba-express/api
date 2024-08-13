package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Wage;

import java.time.LocalDate;

public interface WageRepositoryCustom {
    public Wage getWageBySlaveAndDate(String slaveId, LocalDate date);
}
