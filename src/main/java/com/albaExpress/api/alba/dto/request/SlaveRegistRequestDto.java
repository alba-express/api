package com.albaExpress.api.alba.dto.request;

import com.albaExpress.api.alba.entity.Schedule;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.entity.Workplace;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;
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

    private boolean slaveWageType; // 급여타입 (true, 1 = 시급, false, 0 = 월급)

    private int slaveWageMount; // 시급 = 시급금액, 월급 = 월급금액

    private boolean slaveWageInsurance; // 4대보험 여부 (true, 1 = 적용, false, 0 = 미적용)

    private boolean slaveScheduleType; // 근무시간타입 (true, 1 = 고정시간, false, 0 = 변동시간)

    private List<SlaveRegistScheduleRequestDto> slaveScheduleList; // 근무정보 (근무요일, 근무시작시간, 근무종료시간)

    // SlaveRegistRequestDto --> Entity Slave 로 변환하기
    public Slave dtoToSlaveEntity () {

        Slave slave = Slave.builder()
                                .workplace(Workplace.builder().id(this.workPlaceNumber).build())
                                .slaveName(this.slaveName)
                                .slavePhoneNumber(this.slavePhoneNumber)
                                .slaveBirthday(this.slaveBirthday)
                                .slavePosition(this.slavePosition)
                                .build();

        // SlaveRegistScheduleRequestDto 를 Schedule 로 바꾼 것을 List로 만들어 slave build 객체에 전달
        List<Schedule> schedules = this.slaveScheduleList.stream()
                .map(SlaveRegistScheduleRequestDto -> SlaveRegistScheduleRequestDto.dtoToScheduleEntity(slave))
                .collect(Collectors.toList());

        slave.setScheduleList(schedules);

        return slave;
    }
}
