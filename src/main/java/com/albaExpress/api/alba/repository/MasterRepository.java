package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Master;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterRepository extends JpaRepository<Master, String> {
    Optional<Master> findByMasterEmail(String masterEmail);
}
