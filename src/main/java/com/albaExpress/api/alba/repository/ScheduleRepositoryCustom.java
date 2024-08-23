package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.dto.request.ExtraScheduleRequestDto;
import com.albaExpress.api.alba.dto.response.ScheduleAndLogDto;
import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.entity.ExtraSchedule;
import com.albaExpress.api.alba.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepositoryCustom {

    List<ScheduleSlaveResponseDto> findSlaveBySchedule(String workplaceId, LocalDate date, int dayOfWeek);

    // 지효씨의 추가메서드 컨플릭트시 추가만하면됨
    List<Schedule> findByScheduleDay(int day, String workplaceId, LocalDate date);

    List<ExtraScheduleRequestDto> getExtraSchedule(String workplaceId, LocalDate date);

    List<ScheduleSlaveResponseDto> findSlaveByWorkplaceId(String workplaceId);

    Schedule findScheduleBySlaveId(String slaveId, LocalDate date);

    List<ScheduleAndLogDto> getScheduleAndScheduleLog(String workplaceId, LocalDate date);

}
