package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.MasterRequestDto;
import com.albaExpress.api.alba.dto.request.VerificationCodeRequestDto;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.service.MasterService;
import com.albaExpress.api.alba.service.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private MasterService masterService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody MasterRequestDto masterDto) {
        try {
            Master savedUser = masterService.registerOrUpdateUser(masterDto);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
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
}
