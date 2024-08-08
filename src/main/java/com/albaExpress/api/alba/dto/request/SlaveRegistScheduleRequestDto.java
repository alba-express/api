package com.albaExpress.api.alba.dto.request;

import com.albaExpress.api.alba.entity.Schedule;
import com.albaExpress.api.alba.entity.Slave;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveRegistScheduleRequestDto {

    private int scheduleDay; // 근무요일
    // 근무요일 (월=1, 화=2, 수=3, 목=4, 금=5, 토=6, 일=7)

    private LocalTime startSchedule; // 근무시작시간

    private LocalTime endSchedule; // 근무종료시간;

    // SlaveRegistRequestDto --> Entity Schedule 로 변환하기
    public Schedule dtoToScheduleEntity (Slave slave) {
        return Schedule.builder()
                .scheduleDay(this.scheduleDay)
                .scheduleStart(this.startSchedule)
                .scheduleEnd(this.endSchedule)
                .scheduleUpdateDate(LocalDate.now())
                .slave(slave)
                .build();
    }
}
