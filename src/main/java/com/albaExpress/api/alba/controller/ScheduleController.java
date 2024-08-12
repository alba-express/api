package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.ScheduleRequestDto;
import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/detail")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 해당 날짜 근무자 조회
    @GetMapping("/schedule-manage")
    public ResponseEntity<List<ScheduleSlaveResponseDto>> getSlaveBySchedule(@RequestParam String workplaceId,
                                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                                            @RequestParam int dayOfWeek ) {

        log.info("Fetch workplaceId={}, date={}, dayOfWeek={}", workplaceId, date, dayOfWeek);
        List<ScheduleSlaveResponseDto> scheduleData = scheduleService.findSlaveBySchedule(workplaceId, date, dayOfWeek);
        return ResponseEntity.ok(scheduleData);

    }

    // 일정 추가
    @PostMapping("/schedule-add")
    public ResponseEntity<?> addSchedule(@RequestParam String slaveId,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                         @RequestParam LocalTime startTime, @RequestParam LocalTime endTime) {
        log.info("Add schedule");
        List<ScheduleRequestDto> addedSchedule = scheduleService.addSchedule(slaveId, date, startTime, endTime);

        return ResponseEntity.ok().body(addedSchedule);
    }


}
