package com.albaExpress.api.alba.dto.request;

import com.albaExpress.api.alba.entity.Schedule;
import com.albaExpress.api.alba.entity.Slave;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveModifyScheduleRequestDto {

    private String modifyId; // 이전의 근무리스트 id

    //----------------------------------------------------------------------

    private boolean slaveScheduleType; // 근무시간타입 (true, 1 = 고정시간, false, 0 = 변동시간)

    private List<SlaveRegistScheduleListRequestDto> slaveScheduleList; // 근무목록 (근무요일, 근무시작시간, 근무종료시간)

    // SlaveRegistRequestDto --> Entity Schedule 로 변환하기
    public List<Schedule> dtoToScheduleEntity (Slave slave) {
        return this.slaveScheduleList.stream().map(scheduleList ->
                Schedule.builder()
                        .scheduleType(this.slaveScheduleType)
                        .scheduleDay(scheduleList.getScheduleDay())
                        .scheduleStart(scheduleList.getStartSchedule())
                        .scheduleEnd(scheduleList.getEndSchedule())
                        .scheduleUpdateDate(LocalDate.now())
                        .slave(slave)
                        .build()
        ).collect(Collectors.toList());
    }
}