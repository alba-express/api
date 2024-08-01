package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Slave;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlaveRepository extends JpaRepository<Slave, String>, SlaveRepositoryCustom {
}
