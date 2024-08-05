package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.EmailVerification;
import com.albaExpress.api.alba.entity.Master;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    void deleteByMaster(Master master);

    Optional<EmailVerification> findByMasterAndEmailVerificationCode(Master master, int emailVerificationCode);
}
