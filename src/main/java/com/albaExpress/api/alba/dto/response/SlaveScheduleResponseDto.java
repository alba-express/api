package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.Schedule;
import lombok.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveScheduleResponseDto {
    
    private String slaveScheduleId; // 근무시간 id

    private String scheduleDay; // 근무요일
    // 근무요일 (월=1, 화=2, 수=3, 목=4, 금=5, 토=6, 일=0)

    private String scheduleStart; // 근무시작시간

    private String scheduleEnd; // 근무종료시간;

    // Entity Schedule --> SlaveScheduleResponseDto 로 변환하기
    public SlaveScheduleResponseDto(Schedule schedule) {
        this.slaveScheduleId = schedule.getId();
        this.scheduleDay = setScheduleDay(schedule); // setter 함수
        this.scheduleStart = formatTimeHourToMinute(schedule.getScheduleStart());
        this.scheduleEnd = formatTimeHourToMinute(schedule.getScheduleEnd());
    }

    // DB에 숫자타입으로 저장된 요일값을 문자열로 변환하기
    private String setScheduleDay (Schedule schedule) {

        int day = schedule.getScheduleDay();

        switch (day) {
            case 0 :
                return "일요일";
            case 1 :
                return "월요일";
            case 2 :
                return "화요일";
            case 3 :
                return "수요일";
            case 4 :
                return "목요일";
            case 5 :
                return "금요일";
            case 6 :
                return "토요일";
            default:
                return "";
        }
    }

    // LocalTime 시:분:초 형식에서 --> 시:분 형식으로 꺼내오기
    private static String formatTimeHourToMinute(LocalTime time) {

        // 꺼내온 시간의 형식을 아래와 같이 변환
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH시 mm분");
        String scheduleTime = time.format(formatterTime);

        return scheduleTime;
    }
}
