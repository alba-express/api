package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.SalaryLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryLogRepository extends JpaRepository<SalaryLog, String>, SalaryLogRepositoryCustom {


}
