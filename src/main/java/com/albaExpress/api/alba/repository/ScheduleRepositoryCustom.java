package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.dto.request.ExtraScheduleRequestDto;
import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.entity.ExtraSchedule;
import com.albaExpress.api.alba.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepositoryCustom {

    List<ScheduleSlaveResponseDto> findSlaveBySchedule(String workplaceId, LocalDate date, int dayOfWeek);

    List<ExtraScheduleRequestDto> getExtraSchedule(String workplaceId, LocalDate date);

    List<ScheduleSlaveResponseDto> findSlaveByWorkplaceId(String workplaceId);

}
