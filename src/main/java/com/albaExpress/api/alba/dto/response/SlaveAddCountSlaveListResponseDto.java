package com.albaExpress.api.alba.dto.response;

import lombok.*;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveAddCountSlaveListResponseDto {

    List<SlaveAllSlaveListResponseDto> slaveList; // 조건에 따른 직원리스트 (근무중인 직원리스트 or 퇴사한 직원리스트)

    int totalSlaveCount; // 직원리스트의 직원 수
}
