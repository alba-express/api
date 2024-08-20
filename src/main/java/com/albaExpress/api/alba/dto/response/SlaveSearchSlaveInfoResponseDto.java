package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.Slave;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveSearchSlaveInfoResponseDto {

    private String slaveId; // 직원 id

    private String slaveName; // 직원 이름

    private String slavePosition; // 직원 직책

    private String slavePhoneNumber; // 직원 전화번호

    private LocalDate slaveBirthday; // 직원 생년월일

    private LocalDateTime slaveCreatedAt; // 직원 입사일

    private LocalDateTime slaveFiredDate; // 직원 퇴사일

    private boolean slaveStatus; // 퇴사여부

    private List<SlaveWageResponseDto> wageList; // 직원의 급여정보

    private List<SlaveScheduleResponseDto> scheduleList; // 직원의 근무정보

    private List<SlaveScheduleLogResponseDto> scheduleLogList; // 직원의 근태정보

    public SlaveSearchSlaveInfoResponseDto(Slave slave) {
        this.slaveId = slave.getId();
        this.slaveName = slave.getSlaveName();
        this.slavePosition = slave.getSlavePosition();
        this.slavePhoneNumber = slave.getSlavePhoneNumber();
        this.slaveBirthday = slave.getSlaveBirthday();
        this.slaveCreatedAt = slave.getSlaveCreatedAt();
        this.slaveFiredDate = slave.getSlaveFiredDate();
        this.slaveStatus = slave.getSlaveFiredDate() == null; // 퇴사일자가 없을 경우 true, 퇴사일자가 있으면 false

        // SlaveWageResponseDto를 SlaveOneSlaveInfoResponseDto 의 wageList 로 변환하기
        this.wageList = slave.getWageList().stream().map(SlaveWageResponseDto::new).collect(Collectors.toList());
        // SlaveScheduleResponseDto를 SlaveOneSlaveInfoResponseDto 의 scheduleList 로 변환하기
        this.scheduleList = slave.getScheduleList().stream().map(SlaveScheduleResponseDto::new).collect(Collectors.toList());
        // SlaveScheduleLogResponseDto를 SlaveOneSlaveInfoResponseDto 의 scheduleLogList 로 변환하기
        this.scheduleLogList = slave.getScheduleLogList().stream().map(SlaveScheduleLogResponseDto::new).collect(Collectors.toList());
    }

//    // LocalDateTime 년:월:일 시:분:초 형식에서 --> yyyy년 MM월 dd일 형식으로 변환
//    private static String formatTimeYearToDate(LocalDateTime date) {
//        if (date == null) {
//            return "";
//        }
//
//        // 꺼내온 시간의 형식을 아래와 같이 변환
//        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
//        return date.format(formatterDate);
//    }
}
