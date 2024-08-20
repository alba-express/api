package com.albaExpress.api.alba.dto.response;

import lombok.*;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalaryLogDetailResponseDto {

    private String slaveId;

    private String slaveName;

    private boolean wageInsurance;

    @Setter
    private Long totalSalary;

    private List<SalaryScheduleResponseDto> dtoList;


}
