package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.SlaveRegistRequestDto;
import com.albaExpress.api.alba.service.SlaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/detail")
@RequiredArgsConstructor
public class SlaveController {

    private final SlaveService slaveService;

    // 직원 등록하기
    @PostMapping("/registSlave")
    public ResponseEntity<?> registSlave (@RequestBody SlaveRegistRequestDto dto) {

        // 클라이언트에서 입력한 직원입력정보 조회하기
        // log.info("regist slave Info - {}", dto);

        // slaveService 로 정보처리 위임하기
        try {
            slaveService.serviceRegistSlave(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body("Regist slave success");
    }
}
