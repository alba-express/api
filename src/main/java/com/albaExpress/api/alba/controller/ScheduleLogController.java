package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.entity.ScheduleLog;
import com.albaExpress.api.alba.service.ScheduleLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleLogController {

    @Autowired
    private ScheduleLogService scheduleLogService;

    @PostMapping("/checkin")
    public ScheduleLog checkIn(@RequestBody ScheduleLog scheduleLog) {
        return scheduleLogService.checkIn(scheduleLog);
    }

    @PostMapping("/checkout/{id}")
    public ScheduleLog checkOut(@PathVariable String id) {
        return scheduleLogService.checkOut(id);
    }
}
