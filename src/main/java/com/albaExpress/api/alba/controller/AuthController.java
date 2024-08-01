package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.LoginRequest;
import com.albaExpress.api.alba.dto.request.MasterRequestDto;
import com.albaExpress.api.alba.dto.request.ResetPasswordRequestDto;
import com.albaExpress.api.alba.dto.request.VerificationCodeRequestDto;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.repository.MasterRepository;
import com.albaExpress.api.alba.service.MasterService;
import com.albaExpress.api.alba.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private MasterService masterService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MasterRepository masterRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody MasterRequestDto masterDto) {
        try {
            Master savedUser = masterService.registerOrUpdateUser(masterDto);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            logger.error("Error during registration: {}", e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmailAndSendCode(@RequestParam String email) {
        boolean exists = masterService.emailExists(email);
        if (!exists) {
            emailVerificationService.sendVerificationCode(email);
            return ResponseEntity.ok("{\"message\":\"Verification code sent\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\":\"Email already in use\"}");
        }
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody VerificationCodeRequestDto requestDto) {
        emailVerificationService.sendVerificationCode(requestDto.getEmail());
        return ResponseEntity.ok("{\"message\":\"Verification code sent\"}");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Boolean> verifyCode(@RequestBody VerificationCodeRequestDto requestDto) {
        boolean isValid = emailVerificationService.verifyCode(requestDto);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt with email: {}", loginRequest.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Login successful for email: {}", loginRequest.getEmail());
            return ResponseEntity.ok("{\"message\":\"Login successful\"}");
        } catch (Exception e) {
            logger.error("Login failed for email: {} with error: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Invalid email or password\"}");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        try {
            Master master = masterService.findByMasterEmail(resetPasswordRequestDto.getEmail());
            master.setMasterPassword(passwordEncoder.encode(resetPasswordRequestDto.getPassword()));
            masterRepository.save(master);
            return ResponseEntity.ok("{\"message\":\"Password reset successful\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
