package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.Schedule;
import lombok.*;

import java.time.LocalTime;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveScheduleResponseDto {
    
    private String slaveScheduleId; // 근무시간 id

    private int scheduleDay; // 근무요일
    // 근무요일 (월=1, 화=2, 수=3, 목=4, 금=5, 토=6, 일=7)

    private LocalTime startSchedule; // 근무시작시간

    private LocalTime endSchedule; // 근무종료시간;

    // Entity Schedule --> SlaveScheduleResponseDto 로 변환하기
    public SlaveScheduleResponseDto(Schedule schedule) {
        this.slaveScheduleId = schedule.getId();
        this.scheduleDay = schedule.getScheduleDay();
        this.startSchedule = schedule.getScheduleStart();
        this.endSchedule = schedule.getScheduleEnd();
    }
}
