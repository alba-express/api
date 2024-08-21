package com.albaExpress.api.alba.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkingTimeDto {
    private int week;
    private String slaveId;
    private double workingTime;
    private LocalDate workingDate;
}
