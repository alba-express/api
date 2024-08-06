package com.albaExpress.api.alba.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalarySlaveRequestDto {

    private String slaveId;

    private YearMonth ym;
}
