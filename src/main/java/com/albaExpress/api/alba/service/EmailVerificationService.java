package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.VerificationCodeRequestDto;
import com.albaExpress.api.alba.entity.EmailVerification;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.repository.EmailVerificationRepository;
import com.albaExpress.api.alba.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Random;

@Service
public class EmailVerificationService {
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final int CODE_LENGTH = 4;
    private static final Random RANDOM = new Random();

    public void sendVerificationCode(String email) {
        Master master = masterRepository.findByMasterEmail(email);
        if (master == null) {
            master = new Master();
            master.setMasterEmail(email);
            master = masterRepository.save(master);
        }

        int code = generateVerificationCode();
        EmailVerification verification = EmailVerification.builder()
                .emailVerificationCode(code)
                .emailVerificationExpiryDate(LocalTime.now().plusMinutes(5))
                .master(master)
                .build();

        emailVerificationRepository.deleteByMaster(master);  // 기존 인증 코드 삭제
        emailVerificationRepository.save(verification);

        sendEmail(email, "Your Verification Code", "Your verification code is " + code);
    }

    public boolean verifyCode(VerificationCodeRequestDto requestDto) {
        Master master = masterRepository.findByMasterEmail(requestDto.getEmail());
        if (master == null) {
            throw new IllegalArgumentException("User with email " + requestDto.getEmail() + " not found");
        }

        EmailVerification verification = emailVerificationRepository.findByMasterAndEmailVerificationCode(master, requestDto.getCode());
        if (verification != null && verification.getEmailVerificationExpiryDate().isAfter(LocalTime.now())) {
            emailVerificationRepository.delete(verification);
            master.setEmailVerified(true);
            masterRepository.save(master);
            return true;
        }
        return false;
    }

    private int generateVerificationCode() {
        return RANDOM.nextInt(9000) + 1000; // 1000부터 9999 사이의 랜덤 숫자 생성
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("springemailsender@naver.com");  // 발신자 주소 설정
        mailSender.send(message);
    }
}
