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

    private String slavePosition; // 근무자 직급

    private int wage; // 근무자 시급월급

    private boolean wageType; // 급여종류 true:시급 false: 월급

    private boolean wageInsurance; // 4대보험여부

    private long totalAmount; // 해당 달의 총급여량



}
