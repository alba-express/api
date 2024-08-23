package com.albaExpress.api.alba.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveScheduleLogStatusResponseDto {

    // 해당 상태가 기록된 날짜
    private LocalDate date;

    // 출퇴근 상태를 나타내는 enum (예: 정상, 결근, 지각, 조퇴, 연장)
    private ScheduleLogStatus status;

    // 예정된 출근 시간 (optional)
    private LocalTime expectedStartTime;

    // 예정된 퇴근 시간 (optional)
    private LocalTime expectedEndTime;

    // 실제 출근 시간 (optional)
    private LocalTime actualStartTime;

    // 실제 퇴근 시간 (optional)
    private LocalTime actualEndTime;

    // 생성자: 날짜와 상태만 필요한 기본 생성자
    public SlaveScheduleLogStatusResponseDto(LocalDate date, ScheduleLogStatus status) {
        this.date = date;
        this.status = status;
    }
}
