package com.albaExpress.api.alba.dto.request;

import com.albaExpress.api.alba.entity.ExtraSchedule;
import com.albaExpress.api.alba.entity.Schedule;
import com.albaExpress.api.alba.entity.Slave;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequestDto {

    private String slaveId;
    private String slaveName;
    private String slavePosition;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public ExtraSchedule toEntity() {
        Slave slave = new Slave();

        return ExtraSchedule.builder()
                .extraScheduleDate(this.date)
                .extraScheduleStart(this.startTime)
                .extraScheduleEnd(this.endTime)
                .slave(slave)
                .build();
    }

}
