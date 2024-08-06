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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            return ResponseEntity.badRequest().body("오늘 근무자가 아닙니다.");
        }
        return ResponseEntity.ok().body(slave.getId());
    }

    // 출퇴근 기록
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
    public ResponseEntity<List<SlaveDto>> getTodayEmployees() {
        List<SlaveDto> employees = scheduleLogService.getTodayEmployees();
        return ResponseEntity.ok(employees);
    }
}
