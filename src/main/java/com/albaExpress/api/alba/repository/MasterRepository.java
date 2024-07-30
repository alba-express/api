package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Master;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterRepository extends JpaRepository<Master, String> {
    Master findByMasterEmail(String masterEmail);
}
