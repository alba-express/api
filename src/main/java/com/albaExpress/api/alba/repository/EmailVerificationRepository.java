package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.EmailVerification;
import com.albaExpress.api.alba.entity.Master;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
    EmailVerification findByMasterAndEmailVerificationCode(Master master, int emailVerificationCode);
    void deleteByMaster(Master master);
}
