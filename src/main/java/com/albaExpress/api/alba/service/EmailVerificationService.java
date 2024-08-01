package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.VerificationCodeRequestDto;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationService {

    private final MasterRepository masterRepository;

    @Autowired
    public EmailVerificationService(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }

    public void sendVerificationCode(String email) {
        // Verification code sending logic
    }

    public boolean verifyCode(VerificationCodeRequestDto requestDto) {
        // Verification code checking logic
        return true;
    }

    public boolean verifyEmail(String email) {
        Master master = masterRepository.findByMasterEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        master.setEmailVerified(true);
        masterRepository.save(master);

        return true;
    }
}
