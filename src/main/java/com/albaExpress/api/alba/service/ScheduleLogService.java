package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.response.SlaveDto;
import com.albaExpress.api.alba.entity.Schedule;
import com.albaExpress.api.alba.entity.ScheduleLog;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.repository.ScheduleLogRepository;
import com.albaExpress.api.alba.repository.ScheduleRepository;
import com.albaExpress.api.alba.repository.SlaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleLogService {

    private final ScheduleLogRepository scheduleLogRepository;
    private final SlaveRepository slaveRepository;
    private final ScheduleRepository scheduleRepository;

    // 전화번호와 작업장 ID로 근무자를 검증합니다.
    public Slave verifyPhoneNumber(String phoneNumber, String workplaceId) {
        Slave slave = slaveRepository.getSlaveByPhoneNumber(phoneNumber, workplaceId);
        // 오늘 근무자인지 확인합니다.
        if (slave != null && isWorkingToday(slave.getId())) {
            return slave;
        } else {
            return null;
        }
    }

    // 출근 기록을 저장합니다.
    public ScheduleLog checkIn(String slaveId) throws Exception {
        // 현재 출근 기록이 있는지 확인합니다.
        if (findCurrentLog(slaveId).isPresent()) {
            throw new Exception("이미 출근 기록이 있습니다.");
        }
        // 주어진 slaveId로 근무자를 조회합니다.
        Slave findSlave = slaveRepository.findById(slaveId).orElse(null);
        if (findSlave != null) {
            ScheduleLog scheduleLog = ScheduleLog.builder()
                    .scheduleLogStart(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                    .slave(findSlave)
                    .build();
            return scheduleLogRepository.save(scheduleLog);
        } else {
            throw new Exception("해당 ID의 근무자를 찾을 수 없습니다.");
        }
    }

    // 퇴근 기록을 저장합니다.
    public ScheduleLog checkOut(String logId) throws Exception {
        // 주어진 logId로 출근 기록을 조회합니다.
        ScheduleLog findScheduleLog = scheduleLogRepository.findById(logId).orElse(null);
        if (findScheduleLog != null) {
            findScheduleLog.setScheduleLogEnd(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            return scheduleLogRepository.save(findScheduleLog);
        } else {
            throw new Exception("해당 ID의 출근 기록을 찾을 수 없습니다.");
        }
    }

    // 오늘 근무자인지 확인하는 메서드
    private boolean isWorkingToday(String slaveId) {
        int today = LocalDate.now().getDayOfWeek().getValue();
        // 근무자가 해고된 상태인지 확인합니다.
        Slave slave = slaveRepository.findById(slaveId).orElse(null);
        if (slave == null || slave.getSlaveFiredDate() != null) {
            return false;
        }
        List<Schedule> schedules = scheduleRepository.findBySlaveIdAndScheduleDay(slaveId, today);
        return schedules.stream().anyMatch(schedule -> schedule.getScheduleEndDate() == null);
    }

    // 오늘 근무자 목록을 조회합니다.
    public List<SlaveDto> getTodayEmployees() {
        int today = LocalDate.now().getDayOfWeek().getValue();
        List<Schedule> schedules = scheduleRepository.findByScheduleDay(today);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return schedules.stream()
                .filter(schedule -> schedule.getScheduleEndDate() == null) // 끝나지 않은 스케줄만 필터링
                .filter(schedule -> schedule.getSlave().getSlaveFiredDate() == null) // 해고되지 않은 근무자만 필터링
                .map(schedule -> new SlaveDto(
                        schedule.getSlave().getId(),
                        schedule.getSlave().getSlaveName(),
                        schedule.getSlave().getSlavePosition(),
                        schedule.getScheduleStart() != null ? schedule.getScheduleStart().format(timeFormatter) : "",
                        schedule.getScheduleEnd() != null ? schedule.getScheduleEnd().format(timeFormatter) : ""
                ))
                .sorted(Comparator.comparing(SlaveDto::getScheduleStart)) // 시작 시간을 기준으로 정렬
                .collect(Collectors.toList());
    }

    // 현재 출근 기록을 조회합니다.
    public Optional<ScheduleLog> findCurrentLog(String slaveId) {
        LocalDate today = LocalDate.now();
        return scheduleLogRepository.findFirstBySlaveIdAndScheduleLogStartBetween(
                slaveId,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
    }
}
