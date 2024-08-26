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

import java.time.Clock;
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
    private final WageService wageService;
    private final TimeService timeService;

    // 전화번호로 근무자 확인
    public Slave verifyPhoneNumber(String phoneNumber, String workplaceId) {
        Slave slave = slaveRepository.getSlaveByPhoneNumber(phoneNumber, workplaceId);
        if (slave != null && isWorkingToday(slave.getId())) {
            return slave;
        } else {
            return null;
        }
    }

    // 출근 기록 생성
    public ScheduleLog checkIn(String slaveId) throws Exception {
        if (findCurrentLog(slaveId).isPresent()) {
            throw new Exception("이미 출근 기록이 있습니다.");
        }
        Slave findSlave = slaveRepository.findById(slaveId).orElse(null);
        if (findSlave != null) {
            LocalDateTime now = timeService.getSeoulTime();
            ScheduleLog scheduleLog = ScheduleLog.builder()
                    .scheduleLogStart(now)
                    .slave(findSlave)
                    .build();
            return scheduleLogRepository.save(scheduleLog);
        } else {
            throw new Exception("해당 ID의 근무자를 찾을 수 없습니다.");
        }
    }

    // 퇴근 기록 업데이트
    public ScheduleLog checkOut(String logId) throws Exception {
        ScheduleLog findScheduleLog = scheduleLogRepository.findById(logId).orElse(null);
        if (findScheduleLog != null) {
            LocalDateTime now = timeService.getSeoulTime();
            findScheduleLog.setScheduleLogEnd(now);
            ScheduleLog save = scheduleLogRepository.save(findScheduleLog);

            log.info("스케쥴서비스 퇴근등록시 : {}", save);
            wageService.putSalaryLog(save);

            return save;
        } else {
            throw new Exception("해당 ID의 출근 기록을 찾을 수 없습니다.");
        }
    }

    // 오늘 근무 중인지 확인
    private boolean isWorkingToday(String slaveId) {
        int today = LocalDate.now().getDayOfWeek().getValue();
        if (today == 7) {
            today = 0; // 일요일 처리
        }
        Slave slave = slaveRepository.findById(slaveId).orElse(null);
        if (slave == null || slave.getSlaveFiredDate() != null) {
            return false;
        }
        List<Schedule> schedules = scheduleRepository.findBySlaveIdAndScheduleDay(slaveId, today);
        return schedules.stream().anyMatch(schedule -> schedule.getScheduleEndDate() == null);
    }

    // 특정 근무자의 현재 스케줄 로그 가져오기
    public Optional<ScheduleLog> findCurrentLog(String slaveId) {
        LocalDate today = LocalDate.now();
        return scheduleLogRepository.findFirstBySlaveIdAndScheduleLogStartBetween(
                slaveId,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
    }

    // 특정 날짜에 해당하는 근무자 리스트 가져오기
    public List<SlaveDto> getEmployeesByDate(String workplaceId, LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        if (dayOfWeek == 7) { // 자바의 일요일(7)을 데이터베이스의 일요일(0)로 변환
            dayOfWeek = 0;
        }

        List<Schedule> schedules = scheduleRepository.findByScheduleDay(dayOfWeek, workplaceId, date);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return schedules.stream()
                .filter(schedule -> schedule.getScheduleEndDate() == null || schedule.getScheduleEndDate().isAfter(date))
                .filter(schedule -> schedule.getSlave().getSlaveFiredDate() == null)
                .map(schedule -> {
                    Optional<ScheduleLog> log = findLogForDate(schedule.getSlave().getId(), date);
                    String status;
                    if (log.isPresent()) {
                        if (log.get().getScheduleLogEnd() != null) {
                            status = "퇴근";
                        } else {
                            status = "근무 중";
                        }
                    } else {
                        status = "출근 전";
                    }
                    return new SlaveDto(
                            schedule.getSlave().getId(),
                            schedule.getSlave().getSlaveName(),
                            schedule.getSlave().getSlavePosition(),
                            schedule.getScheduleStart() != null ? schedule.getScheduleStart().format(timeFormatter) : "",
                            schedule.getScheduleEnd() != null ? schedule.getScheduleEnd().format(timeFormatter) : "",
                            status
                    );
                })
                .sorted(Comparator.comparing(SlaveDto::getScheduleStart))
                .collect(Collectors.toList());
    }


    // 특정 날짜에 해당하는 근무자의 스케줄 로그 가져오기
    public Optional<ScheduleLog> findLogForDate(String slaveId, LocalDate date) {
        return scheduleLogRepository.findFirstBySlaveIdAndScheduleLogStartBetween(
                slaveId,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
        );
    }

}
