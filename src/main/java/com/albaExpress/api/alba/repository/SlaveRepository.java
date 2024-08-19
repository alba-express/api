package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Slave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SlaveRepository extends JpaRepository<Slave, String>, SlaveRepositoryCustom {

    Optional<Slave> findBySlavePhoneNumber(String slavePhoneNumber);

    @Override
    <S extends Slave> S save(S entity);

    // Workplace의 workplaceId로 직원 목록 조회
    List<Slave> findByWorkplace_id(String workplaceId);
}
