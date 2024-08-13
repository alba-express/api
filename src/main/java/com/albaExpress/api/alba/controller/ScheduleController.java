package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.ExtraScheduleRequestDto;
import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.entity.ExtraSchedule;
import com.albaExpress.api.alba.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
                                                                             @RequestParam int dayOfWeek) {

        log.info("Fetch workplaceId={}, date={}, dayOfWeek={}", workplaceId, date, dayOfWeek);
        List<ScheduleSlaveResponseDto> scheduleData = scheduleService.findSlaveBySchedule(workplaceId, date, dayOfWeek);
        return ResponseEntity.ok(scheduleData);

    }

    // 해당 날짜 추가 근무자 조회
    @GetMapping("/extraschedule-manage")
    public ResponseEntity<?> getSlaveByExtraSchedule(@RequestParam String workplaceId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date ) {

        try {
            List<ExtraScheduleRequestDto> extraScheduleRequestDto = scheduleService.getExtraSchedule(workplaceId, date);
            log.info("dtoList 크기확인: {}", extraScheduleRequestDto.size());
            for (ExtraScheduleRequestDto scheduleRequestDto : extraScheduleRequestDto) {
                log.info("컨트롤러에서 리턴하기 직전 dto확인: {}", scheduleRequestDto);
            }
            return ResponseEntity.ok(extraScheduleRequestDto);
        } catch (IllegalArgumentException e) {
            log.warn("extraschedule-manage-get에서 일리걸 에러 발생");
            return ResponseEntity.badRequest().body("잘못된 요청입니다: " + e.getMessage());
        } catch (Exception e) {
            log.warn("extraschedule-manage-get에서 일리걸 아닌 에러 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다: " + e.getMessage());
        }

    }

    // 일정 추가
    @PostMapping("/schedule-add")
    public ResponseEntity<?> addSchedule(@RequestBody ExtraScheduleRequestDto dto) {
        log.info("Add schedule = {}", dto);

        try {
            ExtraSchedule extraSchedule = scheduleService.saveExtraSchedule(dto);
//            return ResponseEntity.ok().body(extraSchedule);
            return ResponseEntity.ok().body("등록완료 ok");
        } catch (Exception e) {
            log.warn("schedule-add-post에서 에러남" + e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 사업장 ID로 직원조회
    @GetMapping("/schedule-add")
    public ResponseEntity<?> findSlaveByWorkplaceId(@RequestParam String workplaceId) {

        log.info("Fetch workplaceId={}", workplaceId);
        List<ScheduleSlaveResponseDto> dtoList = scheduleService.findSlaveByWorkplaceId(workplaceId);
        return ResponseEntity.ok(dtoList);
    }

}
