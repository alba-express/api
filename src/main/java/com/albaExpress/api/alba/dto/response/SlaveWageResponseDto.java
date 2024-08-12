package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.Wage;
import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveWageResponseDto {

    private String slaveWageId; // 급여 id

    private boolean slaveWageType; // 급여타입 (true, 1 = 시급, false, 0 = 월급)

    private int slaveWageAmount; // 시급 = 시급금액, 월급 = 월급금액

    private boolean slaveWageInsurance; // 4대보험 여부 (true, 1 = 적용, false, 0 = 미적용)

    // Entity Wage --> SlaveWageResponseDto 로 변환하기
    public SlaveWageResponseDto (Wage wage) {
        this.slaveWageId = wage.getId();
        this.slaveWageType = wage.isWageType();
        this.slaveWageAmount = wage.getWageAmount();
        this.slaveWageInsurance = wage.isWageInsurance();
    }
}
