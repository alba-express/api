package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.entity.ScheduleLog;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.service.ScheduleLogService;
import com.albaExpress.api.alba.dto.request.ScheduleLogVerifyRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@Slf4j
public class ScheduleLogController {

    private final ScheduleLogService scheduleLogService;

//    @PostMapping("/checkin")
//    public ResponseEntity<?> checkIn(@RequestBody ScheduleLog scheduleLog) {
//        ScheduleLog checkIn = scheduleLogService.checkIn(scheduleLog);
//        return ResponseEntity.ok().body(checkIn);
//    }
//
//    @PostMapping("/checkout/{id}")
//    public ResponseEntity<?> checkOut(@PathVariable String id) {
//        ScheduleLog checkOut = scheduleLogService.checkOut(id);
//        return ResponseEntity.ok().body(checkOut);
//    }

    @GetMapping("/verify-phone-number")
    public ResponseEntity<?> verifyPhoneNumber(@RequestParam String phoneNumber, @RequestParam String workplaceId) {

        Slave slave = scheduleLogService.verifyPhoneNumber(phoneNumber, workplaceId);
        if(slave == null) {
            return ResponseEntity.badRequest().body("올바르지 않은 전화번호입니다.");
        }
        return ResponseEntity.ok().body(slave.getId());
    }
}
