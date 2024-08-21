package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.VerificationCodeRequestDto;
import com.albaExpress.api.alba.entity.EmailVerification;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.repository.EmailVerificationRepository;
import com.albaExpress.api.alba.repository.MasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final MasterRepository masterRepository;

    private final EmailVerificationRepository emailVerificationRepository;

    private final EmailService emailService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 기존의 비밀번호 재설정 또는 회원가입을 위한 인증 코드 발송 메서드
    public void sendVerificationCode(String email, boolean isPasswordReset) {
        Optional<Master> optionalMaster = masterRepository.findByMasterEmail(email);

        if (isPasswordReset) {
            if (optionalMaster.isEmpty()) {
                throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
            }
        } else {
            if (optionalMaster.isPresent()) {
                Master master = optionalMaster.get();
                if (master.isEmailVerified()) {
                    throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                }
            }
        }

        String code = generateVerificationCode();
        LocalTime expiryDate = LocalTime.now().plusMinutes(5);

        Master master = optionalMaster.orElseGet(() -> {
            Master newMaster = Master.builder()
                    .masterEmail(email)
                    .emailVerified(false)  // 이메일 인증 여부 false로 설정
                    .build();
            return masterRepository.save(newMaster); // 데이터베이스에 저장
        });

        EmailVerification emailVerification = EmailVerification.builder()
                .master(master)
                .emailVerificationCode(Integer.parseInt(code))
                .emailVerificationExpiryDate(expiryDate)
                .build();

        emailVerificationRepository.save(emailVerification);
        emailService.sendEmail(email, "인증 코드", "인증 코드는 " + code + " 입니다.");

        // 5분 후에 해당 인증 코드 삭제 예약
        scheduler.schedule(() -> {
            emailVerificationRepository.delete(emailVerification);
        }, 5, TimeUnit.MINUTES);
    }

    // 새로운 복구 코드 발송 메서드
    public void sendRecoveryCode(String email) {
        Optional<Master> optionalMaster = masterRepository.findByMasterEmail(email);

        if (optionalMaster.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }

        Master master = optionalMaster.get();
        if (master.getMasterRetired() == null) {
            throw new IllegalArgumentException("이 계정은 탈퇴된 계정이 아닙니다.");
        }

        String code = generateVerificationCode();
        LocalTime expiryDate = LocalTime.now().plusMinutes(5);

        EmailVerification emailVerification = EmailVerification.builder()
                .master(master)
                .emailVerificationCode(Integer.parseInt(code))
                .emailVerificationExpiryDate(expiryDate)
                .build();

        emailVerificationRepository.save(emailVerification);
        emailService.sendEmail(email, "복구 인증 코드", "복구 인증 코드는 " + code + " 입니다.");

        // 5분 후에 해당 인증 코드 삭제 예약
        scheduler.schedule(() -> {
            emailVerificationRepository.delete(emailVerification);
        }, 5, TimeUnit.MINUTES);
    }

    // 기존의 인증 코드 검증 메서드
    public boolean verifyCode(VerificationCodeRequestDto requestDto) {
        Master master = masterRepository.findByMasterEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User with email " + requestDto.getEmail() + " not found"));

        Optional<EmailVerification> optionalVerification = emailVerificationRepository.findByMasterAndEmailVerificationCode(master, Integer.parseInt(requestDto.getCode()));
        if (optionalVerification.isPresent() && optionalVerification.get().getEmailVerificationExpiryDate().isAfter(LocalTime.now())) {
            emailVerificationRepository.delete(optionalVerification.get());
            master.setEmailVerified(true); // 이메일 인증 성공 시
            masterRepository.save(master);
            return true;
        }
        return false;
    }

    // 계정 복구 메서드
    public void recoverUser(String email, String code) {
        VerificationCodeRequestDto requestDto = new VerificationCodeRequestDto();
        requestDto.setEmail(email);
        requestDto.setCode(code);

        boolean isVerified = verifyCode(requestDto);

        if (isVerified) {
            Master master = masterRepository.findByMasterEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

            master.setMasterRetired(null); // 복구 시 retired 상태를 null로 설정
            masterRepository.save(master);
        } else {
            throw new IllegalArgumentException("잘못된 인증 코드입니다.");
        }
    }

    // 인증 코드 생성 메서드
    private String generateVerificationCode() {
        // 4자리 숫자를 String으로 생성
        return String.format("%04d", (int) (Math.random() * 10000));
    }
}
