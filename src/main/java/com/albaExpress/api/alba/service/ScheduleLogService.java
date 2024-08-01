package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.entity.ScheduleLog;
import com.albaExpress.api.alba.repository.ScheduleLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ScheduleLogService {

    @Autowired
    private ScheduleLogRepository scheduleLogRepository;

    public ScheduleLog checkIn(ScheduleLog scheduleLog) {
        scheduleLog.setScheduleLogStart(LocalDateTime.now());
        return scheduleLogRepository.save(scheduleLog);
    }

    public ScheduleLog checkOut(String id) {
        Optional<ScheduleLog> optionalScheduleLog = scheduleLogRepository.findById(id);
        if (optionalScheduleLog.isPresent()) {
            ScheduleLog scheduleLog = optionalScheduleLog.get();
            scheduleLog.setScheduleLogEnd(LocalDateTime.now());
            return scheduleLogRepository.save(scheduleLog);
        } else {
            throw new RuntimeException("로그를 찾을 수 없습니다.");
        }
    }
}
