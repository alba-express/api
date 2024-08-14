package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.SlaveRegistRequestDto;
import com.albaExpress.api.alba.dto.response.SlaveAddCountSlaveListResponseDto;
import com.albaExpress.api.alba.dto.response.SlaveOneSlaveInfoResponseDto;
import com.albaExpress.api.alba.service.SlaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public ResponseEntity<SlaveAddCountSlaveListResponseDto> getAllActiveSlaveList () {
        try {
            SlaveAddCountSlaveListResponseDto activeSlaveList = slaveService.serviceGetAllActiveSlaveList();

            log.info("active slave Info - {}", activeSlaveList);

            return ResponseEntity.ok().body(activeSlaveList);

        } catch (Exception e) {
            // 서버가 클라이언트의 요청을 처리하다가 오류나는 경우
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 퇴사한 직원 전체 조회하기
    @GetMapping("/inactiveSlaveList")
    public ResponseEntity<SlaveAddCountSlaveListResponseDto> getAllInactiveSlaveList () {
        try {
            SlaveAddCountSlaveListResponseDto inactiveSlaveList = slaveService.serviceGetAllInactiveSlaveList();
            return ResponseEntity.ok().body(inactiveSlaveList);

        } catch (Exception e) {
            // 서버가 클라이언트의 요청을 처리하다가 오류나는 경우
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 직원 한 명 조회하기
    @GetMapping("/slave-info/{slaveId}")
    public ResponseEntity<Optional<SlaveOneSlaveInfoResponseDto>> getOneSlave (@PathVariable("slaveId") String slaveId) {

        // 클라이언트에서 서버로 전송한 직원 id
        log.info("slaveId - {}", slaveId);

        try {
            Optional<SlaveOneSlaveInfoResponseDto> selectSlave = slaveService.serviceGetOneSlave(slaveId);

            log.info("have same id Slave Info - {}", selectSlave);

            return ResponseEntity.ok().body(selectSlave);

        } catch (Exception e) {
            // 서버가 클라이언트의 요청을 처리하다가 오류나는 경우
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
