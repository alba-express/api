package com.albaExpress.api.alba.repository;


import com.albaExpress.api.alba.entity.BonusLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface BonusLogRepository extends JpaRepository<BonusLog, String>, BonusLogRepositoryCustom {

    public BonusLog findByBonusDayAndSlaveId(LocalDate bonusDay, String slaveId);

}
