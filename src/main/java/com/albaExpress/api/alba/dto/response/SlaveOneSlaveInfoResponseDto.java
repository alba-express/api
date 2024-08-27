package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.*;
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
public class SlaveOneSlaveInfoResponseDto {

    private String workPlaceNumber; // 직원이 속한 사업장번호

    private String slaveId; // 직원 id

    private String slaveName; // 직원 이름

    private String slavePosition; // 직원 직책

    private String slavePhoneNumber; // 직원 전화번호

    private LocalDate slaveBirthday; // 직원 생년월일

    private String slaveCreatedAt; // 직원 입사일

    private List<SlaveWageResponseDto> wageList; // 직원의 급여정보

    private List<SlaveScheduleResponseDto> scheduleList; // 직원의 근무정보

    private List<SlaveScheduleLogResponseDto> scheduleLogList; // 직원의 근태정보

    public SlaveOneSlaveInfoResponseDto(Slave slave) {
        this.workPlaceNumber = slave.getWorkplace().getId();
        this.slaveId = slave.getId();
        this.slaveName = slave.getSlaveName();
        this.slavePosition = slave.getSlavePosition();
        this.slavePhoneNumber = slave.getSlavePhoneNumber();
        this.slaveBirthday = slave.getSlaveBirthday();
        this.slaveCreatedAt = formatSlaveLocalDateTime(slave.getSlaveCreatedAt());

        LocalDate today = LocalDate.now();

        // SlaveWageResponseDto를 SlaveOneSlaveInfoResponseDto 의 wageList 로 변환하기
        this.wageList = slave.getWageList().stream()
                .filter(wage -> wage.getWageUpdateDate().isBefore(today) && (wage.getWageEndDate() == null || wage.getWageEndDate().isAfter(today)))
                .map(SlaveWageResponseDto::new)
                .collect(Collectors.toList());

        // SlaveScheduleResponseDto를 SlaveOneSlaveInfoResponseDto 의 scheduleList 로 변환하기
        this.scheduleList = slave.getScheduleList().stream()
                .filter(schedule -> schedule.getScheduleEndDate() == null).
                map(SlaveScheduleResponseDto::new)
                .collect(Collectors.toList());

        // SlaveScheduleLogResponseDto를 SlaveOneSlaveInfoResponseDto 의 scheduleLogList 로 변환하기
        this.scheduleLogList = slave.getScheduleLogList().stream().map(SlaveScheduleLogResponseDto::new).collect(Collectors.toList());
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
