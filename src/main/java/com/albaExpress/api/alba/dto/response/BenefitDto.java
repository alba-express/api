package com.albaExpress.api.alba.dto.response;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BenefitDto {
    private int week;
    @Setter
    private double workingTime;
    private String slaveId;
}
