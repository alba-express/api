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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Random;

@Service
public class EmailVerificationService {

    private final MasterRepository masterRepository;
    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    private static final int CODE_LENGTH = 4;
    private static final Random RANDOM = new Random();

    @Autowired
    public EmailVerificationService(MasterRepository masterRepository, JavaMailSender mailSender, EmailVerificationRepository emailVerificationRepository) {
        this.masterRepository = masterRepository;
        this.mailSender = mailSender;
        this.emailVerificationRepository = emailVerificationRepository;
    }

    private int generateVerificationCode() {
        return RANDOM.nextInt(9000) + 1000; // 1000부터 9999 사이의 랜덤 숫자 생성
    }

    @Transactional
    public void sendVerificationCode(String email) {
        Optional<Master> optionalMaster = masterRepository.findByMasterEmail(email);
        if (!optionalMaster.isPresent()) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }

        Master master = optionalMaster.get();

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

    @Transactional
    public boolean verifyCode(VerificationCodeRequestDto requestDto) {
        Optional<Master> optionalMaster = masterRepository.findByMasterEmail(requestDto.getEmail());
        if (!optionalMaster.isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        Master master = optionalMaster.get();
        Optional<EmailVerification> optionalVerification = emailVerificationRepository.findByMasterAndEmailVerificationCode(master, requestDto.getCode());
        if (optionalVerification.isPresent() && optionalVerification.get().getEmailVerificationExpiryDate().isAfter(LocalTime.now())) {
            emailVerificationRepository.delete(optionalVerification.get());
            master.setEmailVerified(true);
            masterRepository.save(master);
            return true;
        }
        return false;
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
