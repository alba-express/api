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

import java.util.Optional;

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
    private TokenProvider tokenProvider;

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
            Optional<Master> optionalMaster = masterService.findByMasterEmailOptional(email);
            if (optionalMaster.isPresent()) {
                Master master = optionalMaster.get();
                if (master.isEmailVerified()) {
                    return ResponseEntity.badRequest().body("{\"message\":\"이미 사용 중인 이메일입니다.\"}");
                } else {
                    emailVerificationService.sendVerificationCode(email);
                    return ResponseEntity.ok("{\"message\":\"인증 코드가 재전송되었습니다.\"}");
                }
            } else {
                emailVerificationService.sendVerificationCode(email);
                return ResponseEntity.ok("{\"message\":\"인증 코드가 이메일로 전송되었습니다.\"}");
            }
        } catch (IllegalArgumentException e) {
            logger.error("이메일 확인 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody VerificationCodeRequestDto requestDto) {
        try {
            emailVerificationService.sendVerificationCode(requestDto.getEmail());
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
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Master master = userDetails.getMaster();

            String token = tokenProvider.createToken(master);

            logger.info("로그인 성공: {}", loginRequest.getEmail());
            return ResponseEntity.ok("{\"message\":\"로그인 성공\", \"token\":\"" + token + "\"}");
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
}
