package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.VerificationCodeRequestDto;
import com.albaExpress.api.alba.entity.EmailVerification;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.repository.EmailVerificationRepository;
import com.albaExpress.api.alba.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class EmailVerificationService {

    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private EmailService emailService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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

    public boolean verifyCode(VerificationCodeRequestDto requestDto) {
        Master master = masterRepository.findByMasterEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User with email " + requestDto.getEmail() + " not found"));

        Optional<EmailVerification> optionalVerification = emailVerificationRepository.findByMasterAndEmailVerificationCode(master, requestDto.getCode());
        if (optionalVerification.isPresent() && optionalVerification.get().getEmailVerificationExpiryDate().isAfter(LocalTime.now())) {
            emailVerificationRepository.delete(optionalVerification.get());
            master.setEmailVerified(true); // 이메일 인증 성공 시
            masterRepository.save(master);
            return true;
        }
        return false;
    }

    private String generateVerificationCode() {
        // 4자리 숫자를 String으로 생성
        return String.format("%04d", (int) (Math.random() * 10000));
    }
}
