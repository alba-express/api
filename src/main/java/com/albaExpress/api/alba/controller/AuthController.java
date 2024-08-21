package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.LoginRequest;
import com.albaExpress.api.alba.dto.request.MasterRequestDto;
import com.albaExpress.api.alba.dto.request.VerificationCodeRequestDto;
import com.albaExpress.api.alba.dto.request.ResetPasswordRequestDto;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.security.CustomUserDetails;
import com.albaExpress.api.alba.security.TokenProvider;
import com.albaExpress.api.alba.service.MasterService;
import com.albaExpress.api.alba.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final MasterService masterService;

    private final EmailVerificationService emailVerificationService;

    private final TokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody MasterRequestDto masterDto) {
        try {
            Master savedUser = masterService.registerOrUpdateUser(masterDto);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            logger.error("회원가입 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmailAndSendCode(@RequestParam String email) {
        try {
            emailVerificationService.sendVerificationCode(email, false);
            return ResponseEntity.ok("{\"message\":\"인증 코드가 이메일로 전송되었습니다.\"}");
        } catch (IllegalArgumentException e) {
            logger.error("이메일 확인 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/check-email-exists")
    public ResponseEntity<?> checkEmailExists(@RequestParam String email) {
        try {
            emailVerificationService.sendVerificationCode(email, true);
            return ResponseEntity.ok("{\"message\":\"인증 코드가 이메일로 전송되었습니다.\"}");
        } catch (IllegalArgumentException e) {
            logger.error("이메일 확인 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody VerificationCodeRequestDto requestDto) {
        try {
            emailVerificationService.sendVerificationCode(requestDto.getEmail(), false);
            return ResponseEntity.ok("{\"message\":\"인증 코드가 이메일로 전송되었습니다.\"}");
        } catch (IllegalArgumentException e) {
            logger.error("인증 코드 전송 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerificationCodeRequestDto requestDto) {
        boolean isValid = emailVerificationService.verifyCode(requestDto);
        if (isValid) {
            return ResponseEntity.ok("{\"message\":\"인증이 완료되었습니다.\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\":\"인증 코드가 잘못되었거나 만료되었습니다.\"}");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        logger.info("로그인 시도 중: {}", loginRequest.getEmail());
        try {
            Authentication authentication = masterService.authenticate(loginRequest);
            String token = tokenProvider.createToken(((CustomUserDetails) authentication.getPrincipal()).getMaster());
            logger.info("로그인 성공: {}", loginRequest.getEmail());
            return ResponseEntity.ok("{\"message\":\"로그인 성공\", \"token\":\"" + token + "\"}");
        } catch (IllegalArgumentException e) {
            logger.error("로그인 실패: {} 오류: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("로그인 실패: {} 오류: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"이메일 또는 비밀번호가 잘못되었습니다.\"}");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        try {
            masterService.resetPassword(resetPasswordRequestDto);
            return ResponseEntity.ok("{\"message\":\"비밀번호가 성공적으로 변경되었습니다.\"}");
        } catch (IllegalArgumentException e) {
            logger.error("비밀번호 변경 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/retire")
    public ResponseEntity<?> retireUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        try {
            masterService.retireUser(email, password);
            return ResponseEntity.ok("{\"message\":\"회원 탈퇴가 완료되었습니다.\"}");
        } catch (IllegalArgumentException e) {
            logger.error("회원 탈퇴 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        try {
            masterService.verifyPassword(email, password);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/send-recover-code")
    public ResponseEntity<String> sendRecoveryCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        emailVerificationService.sendRecoveryCode(email);
        return ResponseEntity.ok("Recovery code sent successfully.");
    }

    @PostMapping("/recover-account")
    public ResponseEntity<String> recoverAccount(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        emailVerificationService.recoverUser(email, code);
        return ResponseEntity.ok("Account recovered successfully.");
    }
}
