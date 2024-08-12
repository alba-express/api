package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.dto.request.ScheduleRequestDto;
import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepositoryCustom {

    List<ScheduleSlaveResponseDto> findSlaveBySchedule(String workplaceId, LocalDate date, int dayOfWeek);

    List<ScheduleRequestDto> addSchedule(String slaveId, LocalDate date, LocalTime startTime, LocalTime endTime);
}
