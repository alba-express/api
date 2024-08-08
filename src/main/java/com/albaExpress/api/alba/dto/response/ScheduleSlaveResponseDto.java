package com.albaExpress.api.alba.dto.response;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class ScheduleSlaveResponseDto {

    private String slaveId;
    private String slaveName;
    private String slavePosition;

    private List<ScheduleDto> schedules;

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Builder
    public static class ScheduleDto {
        private String scheduleId;
        private int scheduleDay;
        private LocalTime scheduleStart;
        private LocalTime scheduleEnd;

    }


}
