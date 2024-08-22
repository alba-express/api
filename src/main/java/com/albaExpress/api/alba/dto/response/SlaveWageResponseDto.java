package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.Wage;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveWageResponseDto {

    private String slaveWageId; // 급여 id

    private String slaveWageInsurance; // 4대보험 여부 (true, 1 = 적용, false, 0 = 미적용)

    private String slaveWageType; // 급여타입 (true, 1 = 시급, false, 0 = 월급)

    private int slaveWageAmount; // 시급 = 시급금액, 월급 = 월급금액

    private LocalDate wageEndDate; // // 급여정보리스트 유효만료

    // Entity Wage --> SlaveWageResponseDto 로 변환하기
    public SlaveWageResponseDto (Wage wage) {
        this.slaveWageId = wage.getId();

        // 4대보험 적용여부에 따른 문자열 변환 (true --> 적용, false --> 미적용)
        this.slaveWageInsurance = wage.isWageInsurance() ? "적용" : "미적용";

        // 급여타입에 따른 문자열 변환 (true --> 시급, false --> 월급)
        this.slaveWageType = wage.isWageType() ? "시급" : "월급";

        this.slaveWageAmount = wage.getWageAmount();

        this.wageEndDate = wage.getWageEndDate();
    }
}
