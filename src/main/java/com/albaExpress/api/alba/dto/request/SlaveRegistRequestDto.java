package com.albaExpress.api.alba.dto.request;

import com.albaExpress.api.alba.entity.Schedule;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.entity.Wage;
import com.albaExpress.api.alba.entity.Workplace;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class SlaveRegistRequestDto {

    private static final Logger log = LoggerFactory.getLogger(SlaveRegistRequestDto.class);

    // 임시
    private String workPlaceNumber; // 사업장번호

    private String slaveName; // 직원 이름

    private String slavePhoneNumber; // 직원 핸드폰번호

    private LocalDate slaveBirthday; // 직원 생일

    private String slavePosition; // 직원 직책

    private List<SlaveRegistWageRequestDto> slaveWageList; // 급여리스트

    private List<SlaveRegistScheduleRequestDto> slaveScheduleList; // 근무정보 (근무요일, 근무시작시간, 근무종료시간)

    // SlaveRegistRequestDto --> Entity Slave 로 변환하기
    public Slave dtoToSlaveEntity () {

        Slave slave = Slave.builder()
                                .workplace(Workplace.builder().id(this.workPlaceNumber).build())
                                .slaveName(this.slaveName)
                                .slavePhoneNumber(this.slavePhoneNumber)
                                .slaveBirthday(this.slaveBirthday)
                                .slavePosition(this.slavePosition)
                                .slaveCreatedAt(LocalDateTime.now())
                                .build();

        // SlaveRegistScheduleRequestDto 를 Schedule 로 바꾼 것을 List로 만들어 slave build 객체에 전달
        List<Schedule> schedules = this.slaveScheduleList.stream()
                .flatMap(slaveRegistScheduleRequestDto ->
                        slaveRegistScheduleRequestDto.dtoToScheduleEntity(slave).stream()
                )
                .collect(Collectors.toList());

        slave.setScheduleList(schedules);

        slave.setScheduleList(schedules);

        // SlaveRegistWageRequestDto 를 Wage 로 바꾼 것을 List로 만들어 slave build 객체에 전달
        List<Wage> wages = this.slaveWageList.stream()
                .map(SlaveRegistWageRequestDto -> SlaveRegistWageRequestDto.dtoToWageEntity(slave))
                .collect(Collectors.toList());

        slave.setWageList(wages);

        return slave;
    }
}
