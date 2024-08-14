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

    private String slaveId; // 직원 사원번호

    private String slaveName; // 직원 이름

    private String slavePosition; // 직원 직책

    private List<SlaveWageResponseDto> slaveWageList; // 급여리스트

    private List<SlaveScheduleResponseDto> slaveScheduleList; // 근무정보 (근무요일, 근무시작시간, 근무종료시간)

    private LocalDateTime slaveCreatedAt; // 직원 입사일자

    private LocalDateTime slaveFiredDate; // 직원 퇴사일자

    // Entity Slave --> SlaveActiveSlaveListResponseDto 로 변환하기
    public SlaveAllSlaveListResponseDto(Slave slave) {
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
