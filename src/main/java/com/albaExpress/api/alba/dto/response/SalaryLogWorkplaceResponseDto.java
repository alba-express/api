package com.albaExpress.api.alba.dto.response;

import lombok.*;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalaryLogWorkplaceResponseDto {

    private long salaryAmount; // 총급여량

    private List<SalaryLogSlaveResponseDto> logList; // 급여가 있는 근무자별 급여량

}
