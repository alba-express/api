package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.Slave;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveAllSlaveListResponseDto {

    private String workPlaceNumber; // 사업장번호

    private String slaveId; // 직원 사원번호

    private String slaveName; // 직원 이름

    private String slavePosition; // 직원 직책

    private List<SlaveWageResponseDto> slaveWageList; // 급여리스트

    private List<SlaveScheduleResponseDto> slaveScheduleList; // 근무정보 (근무요일, 근무시작시간, 근무종료시간)

    private String slaveCreatedAt; // 직원 입사일자

    private String slaveFiredDate; // 직원 퇴사일자

    // Entity Slave --> SlaveActiveSlaveListResponseDto 로 변환하기
    public SlaveAllSlaveListResponseDto(Slave slave) {
        this.workPlaceNumber = slave.getWorkplace().getId();
        this.slaveId = slave.getId();
        this.slaveName = slave.getSlaveName();
        this.slavePosition = slave.getSlavePosition();

        // SlaveWageResponseDto를 SlaveActiveSlaveListResponseDto 의 slaveWageList 로 변환하기
        this.slaveWageList = slave.getWageList().stream().filter(wage -> wage.getWageUpdateDate().isBefore(LocalDate.now()) && (wage.getWageEndDate() == null || wage.getWageEndDate().isAfter(LocalDate.now()))).map(SlaveWageResponseDto::new).collect(Collectors.toList());
        // SlaveScheduleResponseDto를 SlaveActiveSlaveListResponseDto 의 slaveScheduleList 로 변환하기
        this.slaveScheduleList = slave.getScheduleList().stream().filter(schedule -> schedule.getScheduleEndDate() == null).map(SlaveScheduleResponseDto::new).collect(Collectors.toList());

        this.slaveCreatedAt = formatSlaveLocalDateTime(slave.getSlaveCreatedAt());
        this.slaveFiredDate = formatSlaveLocalDateTime(slave.getSlaveFiredDate());
    }
    // LocalDateTime 년:월:일 시:분:초 형식에서 --> 년:월:일 형식으로 꺼내오기
    public static String formatSlaveLocalDateTime(LocalDateTime time) {

        // 시간이 null인 경우
        if (time == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        return time.format(formatter);
    }
}
