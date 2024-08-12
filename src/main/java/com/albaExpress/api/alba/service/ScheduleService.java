package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.ScheduleRequestDto;
import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    // 해당 날짜 근무자 조회
    public List<ScheduleSlaveResponseDto> findSlaveBySchedule(String workplaceId, LocalDate date, int dayOfWeek) {

        List<ScheduleSlaveResponseDto> result = scheduleRepository.findSlaveBySchedule(workplaceId, date, dayOfWeek);
        log.info("result: {} ", result);

        return result;
    }

    // 일정 추가
    public List<ScheduleRequestDto> addSchedule(String slaveId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<ScheduleRequestDto> addedSchedule = scheduleRepository.addSchedule(slaveId, date, startTime, endTime);
        log.info("addedSchedule: {} ", addedSchedule);

        return addedSchedule;

    }
}
