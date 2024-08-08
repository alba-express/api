package com.albaExpress.api.alba.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BonusRequestDto {

    private String slaveId;

    private int amount;

    private LocalDate workDate;

}
