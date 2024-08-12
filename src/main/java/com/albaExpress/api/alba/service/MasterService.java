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
import com.albaExpress.api.alba.dto.request.VerificationCodeRequestDto;

import java.time.LocalDateTime;
import java.util.Optional;

import com.albaExpress.api.alba.dto.request.LoginRequest;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailVerificationService emailVerificationService;

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
            logger.info("Registering new user with email: {}", masterDto.getEmail());
            // 새로 등록
            master = Master.builder()
                    .masterEmail(masterDto.getEmail())
                    .masterPassword(passwordEncoder.encode(masterDto.getPassword()))
                    .masterName(masterDto.getName())
                    .emailVerified(false) // 이메일 인증 완료 후 회원가입 진행
                    .build();
        } else {
            logger.info("Updating existing user with email: {}", masterDto.getEmail());
            // 기존 사용자 업데이트
            master = optionalMaster.get();
            master.setMasterPassword(passwordEncoder.encode(masterDto.getPassword()));
            master.setMasterName(masterDto.getName());
            master.setMasterCreatedAt(LocalDateTime.now());

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

    public void retireUser(String email, String password) {
        Master master = masterRepository.findByMasterEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        if (!passwordEncoder.matches(password, master.getMasterPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        master.setMasterRetired(LocalDateTime.now());
        masterRepository.save(master);
    }

    public Authentication authenticate(LoginRequest loginRequest) {
        Master master = findByMasterEmail(loginRequest.getEmail());
        if (master.getMasterRetired() != null) {
            throw new IllegalArgumentException("탈퇴한 회원입니다.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
    public void verifyPassword(String email, String password) {
        Master master = masterRepository.findByMasterEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        if (!passwordEncoder.matches(password, master.getMasterPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
    }
    public void recoverAccount(String email, String verificationCode) {
        boolean isVerified = emailVerificationService.verifyCode(
                new VerificationCodeRequestDto(email, verificationCode)
        );

        if (!isVerified) {
            throw new IllegalArgumentException("Invalid verification code.");
        }

        Master master = masterRepository.findByMasterEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        if (master.getMasterRetired() == null) {
            throw new IllegalArgumentException("Account is already active.");
        }

        master.setMasterRetired(null); // 복구 시 master_retired 필드 값을 null로 변경
        masterRepository.save(master);
    }

}
