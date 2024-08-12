package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.dto.request.SlaveRegistScheduleRequestDto;
import com.albaExpress.api.alba.dto.request.SlaveRegistWageRequestDto;
import com.albaExpress.api.alba.entity.Slave;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveActiveSlaveListResponseDto {

    private String slaveId; // 직원 사원번호

    private String slaveName; // 직원 이름

    private String slavePosition; // 직원 직책

    private List<SlaveWageResponseDto> slaveWageList; // 급여리스트

    private List<SlaveScheduleResponseDto> slaveScheduleList; // 근무정보 (근무요일, 근무시작시간, 근무종료시간)

    private LocalDateTime slaveCreatedAt; // 직원 입사일자

    private LocalDateTime slaveFiredDate; // 직원 퇴사일자

    // Entity Slave --> SlaveActiveSlaveListResponseDto 로 변환하기
    public SlaveActiveSlaveListResponseDto(Slave slave) {

        this.slaveId = slave.getId();
        this.slaveName = slave.getSlaveName();
        this.slavePosition = slave.getSlavePosition();

        // SlaveWageResponseDto를 SlaveActiveSlaveListResponseDto 의 slaveWageList 로 변환하기
        this.slaveWageList = slave.getWageList().stream().map(SlaveWageResponseDto::new).collect(Collectors.toList());
        // SlaveScheduleResponseDto를 SlaveActiveSlaveListResponseDto 의 slaveScheduleList 로 변환하기
        this.slaveScheduleList = slave.getScheduleList().stream().map(SlaveScheduleResponseDto::new).collect(Collectors.toList());

        this.slaveCreatedAt = slave.getSlaveCreatedAt();
        this.slaveFiredDate = slave.getSlaveFiredDate();
    }
}
