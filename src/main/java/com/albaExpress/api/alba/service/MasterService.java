package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.MasterRequestDto;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MasterService {
    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean emailExists(String email) {
        return masterRepository.findByMasterEmail(email) != null;
    }

    public Master registerOrUpdateUser(MasterRequestDto masterDto) {
        Master master = masterRepository.findByMasterEmail(masterDto.getEmail());
        if (master == null) {
            // 새로 등록
            master = Master.builder()
                    .masterEmail(masterDto.getEmail())
                    .masterPassword(passwordEncoder.encode(masterDto.getPassword()))
                    .masterName(masterDto.getName())
                    .emailVerified(true) // 이메일 인증 완료 후 회원가입 진행
                    .build();
        } else {
            // 기존 사용자 업데이트
            master.setMasterPassword(passwordEncoder.encode(masterDto.getPassword()));
            master.setMasterName(masterDto.getName());
        }

        return masterRepository.save(master);
    }
}
