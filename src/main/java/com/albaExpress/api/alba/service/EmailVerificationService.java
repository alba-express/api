package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.VerificationCodeRequestDto;
import com.albaExpress.api.alba.entity.EmailVerification;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.repository.EmailVerificationRepository;
import com.albaExpress.api.alba.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Random;

@Service
public class EmailVerificationService {

    private final MasterRepository masterRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final int CODE_LENGTH = 4;
    private static final Random RANDOM = new Random();

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    public EmailVerificationService(MasterRepository masterRepository) {
        this.masterRepository = masterRepository;
    }

    private int generateVerificationCode() {
        return RANDOM.nextInt(9000) + 1000; // 1000부터 9999 사이의 랜덤 숫자 생성
    }

    public void sendVerificationCode(String email) {
        Optional<Master> optionalMaster = masterRepository.findByMasterEmail(email);
        Master master = optionalMaster.orElseGet(() -> {
            Master newMaster = new Master();
            newMaster.setMasterEmail(email);
            return masterRepository.save(newMaster);
        });

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
        Optional<Master> optionalMaster = masterRepository.findByMasterEmail(requestDto.getEmail());
        if (!optionalMaster.isPresent()) {
            throw new IllegalArgumentException("User with email " + requestDto.getEmail() + " not found");
        }

        Master master = optionalMaster.get();
        EmailVerification verification = emailVerificationRepository.findByMasterAndEmailVerificationCode(master, requestDto.getCode());
        if (verification != null && verification.getEmailVerificationExpiryDate().isAfter(LocalTime.now())) {
            emailVerificationRepository.delete(verification);
            master.setEmailVerified(true);
            masterRepository.save(master);
            return true;
        }
        return false;
    }

    public boolean verifyEmail(String email) {
        Master master = masterRepository.findByMasterEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        master.setEmailVerified(true);
        masterRepository.save(master);

        return true;
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
