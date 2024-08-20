package com.albaExpress.api.alba.dto.request;

import com.albaExpress.api.alba.entity.ExtraSchedule;
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
public class ExtraScheduleRequestDto {

    private String slaveId;
    private String slaveName;
    private String slavePosition;

    private String id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public ExtraSchedule toEntity(Slave slave) {
        return ExtraSchedule.builder()
//                .slave(this.slaveName)
                .extraScheduleDate(this.date)
                .extraScheduleStart(this.startTime)
                .extraScheduleEnd(this.endTime)
                .slave(slave)
                .build();
    }

}
