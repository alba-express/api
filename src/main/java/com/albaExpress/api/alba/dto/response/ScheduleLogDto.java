package com.albaExpress.api.alba.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleLogDto {
    private String id;
    private LocalDateTime scheduleLogStart;
    private LocalDateTime scheduleLogEnd;
    private SlaveDto slave;
}
