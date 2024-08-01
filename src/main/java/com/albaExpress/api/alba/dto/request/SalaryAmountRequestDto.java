package com.albaExpress.api.alba.dto.request;


import lombok.*;

import java.time.YearMonth;

@Getter @ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalaryAmountRequestDto {
    private String workplaceId;
    private YearMonth ym;
}
