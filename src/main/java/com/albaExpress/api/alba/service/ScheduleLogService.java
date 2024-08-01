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

//    public ScheduleLog checkIn(ScheduleLog scheduleLog) {
//        scheduleLog.setScheduleLogStart(LocalDateTime.now());
//        return scheduleLogRepository.save(scheduleLog);
//    }
//
//    public ScheduleLog checkOut(String id) {
//        Optional<ScheduleLog> optionalScheduleLog = scheduleLogRepository.findById(id);
//        if (optionalScheduleLog.isPresent()) {
//            ScheduleLog scheduleLog = optionalScheduleLog.get();
//            scheduleLog.setScheduleLogEnd(LocalDateTime.now());
//            return scheduleLogRepository.save(scheduleLog);
//        } else {
//            throw new RuntimeException("로그를 찾을 수 없습니다.");
//        }
//    }

    public Slave verifyPhoneNumber(String phoneNumber, String workplaceId) {
        Slave slave = slaveRepository.getSlaveByPhoneNumber(phoneNumber, workplaceId);



        return slave;
    }
}
