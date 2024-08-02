package com.albaExpress.api.alba.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalaryLogSlaveResponseDto {

    private String slaveId; // 근무자 아이디

    private String slaveName; // 근무자 이름

    private LocalDate salaryDate; //근무일

    private long salaryAmount; // 해당근무일의 급여량

}
