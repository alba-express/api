package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.entity.ScheduleLog;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.repository.ScheduleLogRepository;
import com.albaExpress.api.alba.repository.SlaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleLogService {

    private final ScheduleLogRepository scheduleLogRepository;

    private final SlaveRepository slaveRepository;

    // 전화번호 검증
    public Slave verifyPhoneNumber(String phoneNumber, String workplaceId) {
        Slave slave = slaveRepository.getSlaveByPhoneNumber(phoneNumber, workplaceId);

        return slave;
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
}
