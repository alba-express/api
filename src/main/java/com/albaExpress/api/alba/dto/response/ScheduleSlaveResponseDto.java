package com.albaExpress.api.alba.dto.response;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    private String scheduleId;
    private int scheduleDay;
    private LocalTime scheduleStart;
    private LocalTime scheduleEnd;


}
