package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.ScheduleLog;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveScheduleLogResponseDto {

    private String scheduleLogId; // 근태 id

    private LocalDateTime scheduleLogStart; // 출근시간

    private LocalDateTime scheduleLogEnd; // 퇴근시간

    // Entity ScheduleLog --> SlaveScheduleLogResponseDto 로 변환하기
    public SlaveScheduleLogResponseDto (ScheduleLog scheduleLog) {
        this.scheduleLogId = scheduleLog.getId();
        this.scheduleLogStart = scheduleLog.getScheduleLogStart();
        this.scheduleLogEnd = scheduleLog.getScheduleLogEnd();
    }
}
