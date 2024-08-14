package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.ExtraScheduleRequestDto;
import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.entity.ExtraSchedule;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.repository.ExtraScheduleRepository;
import com.albaExpress.api.alba.repository.ScheduleRepository;
import com.albaExpress.api.alba.repository.SlaveRepository;
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

    private final ExtraScheduleRepository extraScheduleRepository;

    private final SlaveRepository slaveRepository;

    // 해당 날짜 근무자 조회
    public List<ScheduleSlaveResponseDto> findSlaveBySchedule(String workplaceId, LocalDate date, int dayOfWeek) {

        List<ScheduleSlaveResponseDto> result = scheduleRepository.findSlaveBySchedule(workplaceId, date, dayOfWeek);
        log.info("result: {} ", result);

        return result;
    }

    // 추가 일정 조회
    public List<ExtraScheduleRequestDto> getExtraSchedule(String workplaceId, LocalDate date) {
        List<ExtraScheduleRequestDto> addedSchedule = scheduleRepository.getExtraSchedule(workplaceId, date);
        log.info("추가 일정: {} ", addedSchedule);

        return addedSchedule;
    }

    // 추가 일정 등록
    public ExtraSchedule saveExtraSchedule(ExtraScheduleRequestDto dto) {

        Slave slave = slaveRepository.findById(dto.getSlaveId()).orElseThrow();

        // 일정이 이미 존재하는지 확인
//        boolean exists = extraScheduleRepository.existsBySlaveAndDate(slave, dto.getDate());
//        if (exists) {
//            throw new IllegalStateException("이미 해당 날짜에 추가된 일정이 존재합니다.");
//        }

        ExtraSchedule extraSchedule = dto.toEntity(slave);
        extraSchedule.setSlave(slave);
        ExtraSchedule savedExtraSchedule = extraScheduleRepository.save(extraSchedule);
        log.info("saved extraSchedule: {}", savedExtraSchedule);
        return savedExtraSchedule;
    }

    // 사업장 ID로 직원조회
    public List<ScheduleSlaveResponseDto> findSlaveByWorkplaceId(String workplaceId) {
        List<ScheduleSlaveResponseDto> result = scheduleRepository.findSlaveByWorkplaceId(workplaceId);
        log.info("result: {} ", result);

        return result;
    }

}
