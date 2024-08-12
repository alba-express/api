package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.SlaveRegistRequestDto;
import com.albaExpress.api.alba.dto.response.SlaveActiveSlaveListResponseDto;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.service.SlaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
         log.info("regist slave Info - {}", dto);

        // slaveService 로 정보처리 위임하기
        try {
            slaveService.serviceRegistSlave(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body("{\"message\":\"Regist slave success\"}");
    }

    // 근무중인 직원 전체 조회하기
    @GetMapping("/activeSlaveList")
    public ResponseEntity<List<SlaveActiveSlaveListResponseDto>> getAllActiveSlaveList () {
        try {
            List<SlaveActiveSlaveListResponseDto> activeSlaveList = slaveService.serviceGetAllActiveSlaveList();
            return ResponseEntity.ok().body(activeSlaveList);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 퇴사한 직원 전체 조회하기
    @GetMapping("inactiveSlaveList")
    public ResponseEntity<List<SlaveActiveSlaveListResponseDto>> getAllInactiveSlaveList () {
        try {
            List<SlaveActiveSlaveListResponseDto> inactiveSlaveList = slaveService.serviceGetAllInactiveSlaveList();
            return ResponseEntity.ok().body(inactiveSlaveList);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
