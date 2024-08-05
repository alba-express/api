package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.MasterRequestDto;
import com.albaExpress.api.alba.dto.request.ResetPasswordRequestDto;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MasterService {

    private static final Logger logger = LoggerFactory.getLogger(MasterService.class);

    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean emailExists(String email) {
        return masterRepository.findByMasterEmail(email).isPresent();
    }

    public Optional<Master> findByMasterEmailOptional(String email) {
        return masterRepository.findByMasterEmail(email);
    }

    public Master findByMasterEmail(String email) {
        return masterRepository.findByMasterEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public Master registerOrUpdateUser(MasterRequestDto masterDto) {
        logger.info("Register or update user with email: {}", masterDto.getEmail());
        Optional<Master> optionalMaster = masterRepository.findByMasterEmail(masterDto.getEmail());
        Master master;
        if (optionalMaster.isEmpty()) {
            throw new IllegalArgumentException("Email not verified or user not found");
        } else {
            logger.info("Updating existing user with email: {}", masterDto.getEmail());
            // 기존 사용자 업데이트
            master = optionalMaster.get();
            master.setMasterPassword(passwordEncoder.encode(masterDto.getPassword()));
            master.setMasterName(masterDto.getName());
            if (master.getMasterCreatedAt() == null) {
                master.setMasterCreatedAt(LocalDateTime.now());
            }
        }

        return masterRepository.save(master);
    }

    public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) {
        Master master = masterRepository.findByMasterEmail(resetPasswordRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + resetPasswordRequestDto.getEmail()));

        // 비밀번호 업데이트
        master.setMasterPassword(passwordEncoder.encode(resetPasswordRequestDto.getPassword()));
        masterRepository.save(master);
    }
}
