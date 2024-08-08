package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
                                                                            @RequestParam LocalDate date,
                                                                            @RequestParam int dayOfWeek ) {
        List<ScheduleSlaveResponseDto> scheduleData = scheduleService.findSlaveBySchedule(workplaceId, date, dayOfWeek);
        return ResponseEntity.ok(scheduleData);

    }


}
