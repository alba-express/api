package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.CheckInRequestDto;
import com.albaExpress.api.alba.dto.response.SlaveDto;
import com.albaExpress.api.alba.entity.ScheduleLog;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.service.ScheduleLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Slf4j
public class ScheduleLogController {

    private final ScheduleLogService scheduleLogService;

    // 전화번호 검증
    @GetMapping("/verify-phone-number")
    public ResponseEntity<?> verifyPhoneNumber(@RequestParam String phoneNumber, @RequestParam String workplaceId) {
        Slave slave = scheduleLogService.verifyPhoneNumber(phoneNumber, workplaceId);
        if (slave == null) {
            return ResponseEntity.badRequest().body("오늘 근무자가 아닙니다."); // 구체적인 오류 메시지
        }
        return ResponseEntity.ok().body(Collections.singletonMap("slaveId", slave.getId())); // JSON 형태로 반환
    }

    // 출근 기록
    @PostMapping("/checkin")
    public ResponseEntity<?> checkIn(@RequestBody CheckInRequestDto request) {
        try {
            ScheduleLog scheduleLog = scheduleLogService.checkIn(request.getSlaveId());
            return new ResponseEntity<>(scheduleLog, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Check-in Error: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 퇴근 기록
    @PostMapping("/checkout")
    public ResponseEntity<?> checkOut(@RequestBody Map<String, String> request) {
        String logId = request.get("logId");
        try {
            ScheduleLog scheduleLog = scheduleLogService.checkOut(logId);
            return new ResponseEntity<>(scheduleLog, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Check-out Error: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 오늘 근무자 목록 조회
    @GetMapping("/employees")
    public ResponseEntity<List<SlaveDto>> getTodayEmployees(@RequestParam(name = "workplaceId") String workplaceId) {
        if (workplaceId == null || workplaceId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList()); // 유효하지 않은 workplaceId에 대한 응답
        }

        List<SlaveDto> employees = scheduleLogService.getTodayEmployees(workplaceId);
        return ResponseEntity.ok(employees);
    }

    // 현재 출근 로그 조회
    @GetMapping("/current-log")
    public ResponseEntity<?> getCurrentLog(@RequestParam String slaveId) {
        Optional<ScheduleLog> currentLog = scheduleLogService.findCurrentLog(slaveId);
        if (currentLog.isPresent()) {
            return ResponseEntity.ok(currentLog.get());
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("현재 출근 로그가 없습니다.");
        }
    }

    // 서버 시간 조회
    @GetMapping("/server-time")
    public ResponseEntity<LocalDateTime> getServerTime() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        return ResponseEntity.ok(now);
    }
}
