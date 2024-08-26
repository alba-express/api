package com.albaExpress.api.alba.dto.request;

import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.entity.Wage;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveModifyWageRequestDto {

    private String slaveWageId; // 이전의 급여리스트 id

    private boolean slaveWageType; // 급여타입 (true, 1 = 시급, false, 0 = 월급)

    private int slaveWageAmount; // 시급 = 시급금액, 월급 = 월급금액

    private boolean slaveWageInsurance; // 4대보험 여부 (true, 1 = 적용, false, 0 = 미적용)

    // SlaveRegistWageRequestDto --> Entity Wage 로 변환하기
    public Wage dtoToWageEntity (Slave slave) {
        return Wage.builder()
                .wageType(this.slaveWageType)
                .wageAmount(this.slaveWageAmount)
                .wageInsurance(this.slaveWageInsurance)
                .wageUpdateDate(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue() + 1, 1))
                .slave(slave)
                .build();
    }
}
