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

    // 전화번호 검증
    public Slave verifyPhoneNumber(String phoneNumber, String workplaceId) {
        Slave slave = slaveRepository.getSlaveByPhoneNumber(phoneNumber, workplaceId);

        // 오늘 근무자인지 확인
        if (slave != null && isWorkingToday(slave.getId())) {
            return slave;
        } else {
            return null;
        }
    }

    // 출퇴근 기록
    public ScheduleLog checkIn(String slaveId) throws Exception {
        Slave findSlave = slaveRepository.findById(slaveId).orElse(null);
        if (findSlave != null) {
            ScheduleLog scheduleLog = ScheduleLog.builder()
                    .scheduleLogStart(LocalDateTime.now())
                    .slave(findSlave)
                    .build();
            return scheduleLogRepository.save(scheduleLog);
        } else {
            throw new Exception("존재하지 않는 slaveId입니다.");
        }
    }

    public ScheduleLog checkOut(String logId) throws Exception {
        Optional<ScheduleLog> optionalScheduleLog = scheduleLogRepository.findById(logId);
        if (optionalScheduleLog.isPresent()) {
            ScheduleLog scheduleLog = optionalScheduleLog.get();
            scheduleLog.setScheduleLogEnd(LocalDateTime.now());
            return scheduleLogRepository.save(scheduleLog);
        } else {
            throw new Exception("로그를 찾을 수 없습니다.");
        }
    }

    // 오늘 근무자 목록 조회
    public List<SlaveDto> getTodayEmployees() {
        int today = LocalDate.now().getDayOfWeek().getValue(); // 월요일 = 1, 일요일 = 7
        List<Slave> slaves = scheduleRepository.findSlavesByWorkday(today);
        return slaves.stream()
                .map(slave -> new SlaveDto(slave.getId(), slave.getSlaveName(), slave.getSlavePosition()))
                .collect(Collectors.toList());
    }

    // 오늘 근무자인지 확인하는 메서드
    private boolean isWorkingToday(String slaveId) {
        int today = LocalDate.now().getDayOfWeek().getValue();
        List<Schedule> schedules = scheduleRepository.findBySlaveIdAndDay(slaveId, today);
        return !schedules.isEmpty();
    }
}
