package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.CheckInRequestDto;
import com.albaExpress.api.alba.dto.response.SlaveDto;
import com.albaExpress.api.alba.entity.ScheduleLog;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.service.ScheduleLogService;
import com.albaExpress.api.alba.service.TimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@Slf4j
public class ScheduleLogController {

    private final ScheduleLogService scheduleLogService;
    private final TimeService timeService;

    // 전화번호로 근무자 확인
    @GetMapping("/verify-phone-number")
    public ResponseEntity<?> verifyPhoneNumber(@RequestParam String phoneNumber, @RequestParam String workplaceId) {
        Slave slave = scheduleLogService.verifyPhoneNumber(phoneNumber, workplaceId);
        if (slave == null) {
            return ResponseEntity.badRequest().body("오늘 근무자가 아닙니다.");
        }
        return ResponseEntity.ok().body(Collections.singletonMap("slaveId", slave.getId()));
    }

    // 출근 기록 생성
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

    // 퇴근 기록 업데이트
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

    // 특정 날짜에 해당하는 근무자 리스트 가져오기
    @GetMapping("/employees")
    public ResponseEntity<List<SlaveDto>> getEmployeesByDate(
            @RequestParam(name = "workplaceId") String workplaceId,
            @RequestParam(name = "date", required = false) String date) {

        if (workplaceId == null || workplaceId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<SlaveDto> employees;
        if (date == null || date.trim().isEmpty()) {
            employees = scheduleLogService.getEmployeesByDate(workplaceId, LocalDate.now());
        } else {
            LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            employees = scheduleLogService.getEmployeesByDate(workplaceId, parsedDate);
        }

        return ResponseEntity.ok(employees);
    }

    // 특정 근무자의 현재 스케줄 로그 가져오기
    @GetMapping("/current-log")
    public ResponseEntity<?> getCurrentLog(@RequestParam String slaveId) {
        Optional<ScheduleLog> currentLog = scheduleLogService.findCurrentLog(slaveId);
        if (currentLog.isPresent()) {
            return ResponseEntity.ok(currentLog.get());
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("현재 출근 로그가 없습니다.");
        }
    }

    // 서버의 현재 시간 가져오기
    @GetMapping("/server-time")
    public ResponseEntity<LocalDateTime> getServerTime() {
        try {
            LocalDateTime now = timeService.getSeoulTime();
            return ResponseEntity.ok(now);
        } catch (IOException e) {
            log.error("Error fetching server time: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
