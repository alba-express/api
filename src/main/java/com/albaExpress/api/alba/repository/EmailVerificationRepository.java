package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.EmailVerification;
import com.albaExpress.api.alba.entity.Master;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    @Transactional
    void deleteByMaster(Master master);

    Optional<EmailVerification> findByMasterAndEmailVerificationCode(Master master, int code);
}
