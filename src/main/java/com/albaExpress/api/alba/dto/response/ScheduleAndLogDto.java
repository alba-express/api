package com.albaExpress.api.alba.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleAndLogDto {
    private LocalTime scheduleLogStart;
    private LocalTime scheduleLogEnd;
    private LocalTime scheduleStart;
    private LocalTime scheduleEnd;
    private String slaveName;
    private String slavePosition;
    @Setter
    private String dailyAtt;
}
