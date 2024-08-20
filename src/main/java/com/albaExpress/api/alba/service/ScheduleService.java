package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.ExtraScheduleRequestDto;
import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.entity.ExtraSchedule;
import com.albaExpress.api.alba.entity.Schedule;
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
import java.util.NoSuchElementException;

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
    public ExtraSchedule saveExtraSchedule(ExtraScheduleRequestDto dto) throws Exception {

        Slave slave = slaveRepository.findById(dto.getSlaveId()).orElseThrow();

        // 추가 일정 확인
        ExtraSchedule existsExtraSchedule = extraScheduleRepository.findByExtraScheduleDateAndSlaveId(dto.getDate(), slave.getId());
        log.info("Checking for existing schedule: {}", existsExtraSchedule);
        if (existsExtraSchedule != null) {
            throw new IllegalStateException("이미 해당 날짜에 추가 일정이 존재합니다.");
        }
//        else if (dto.getStartTime().isAfter(dto.getEndTime())) {
//            throw new Exception("올바르지 않은 근무시간입니다.");
//        }


        // 조건 필요하면 추가
        // 원래 근무 종료 시간보다 추가일정 시작 시간이 같거나 뒤에 시작
        // 원래 근무 시작 시간보다 추가일정 종료 시간이 같거나 전에 시작

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

    public void deleteExtraSchedule(String id) {
        if (extraScheduleRepository.existsById(id)) {
            extraScheduleRepository.deleteById(id);
            log.info("일정 ID {}가 삭제되었습니다.", id);
        } else {
            log.warn("삭제하려는 일정 ID {}가 존재하지 않습니다.", id);
            throw new NoSuchElementException("삭제하려는 일정이 존재하지 않습니다.");
        }
    }
}
