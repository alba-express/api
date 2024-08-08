package com.albaExpress.api.alba.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalaryScheduleResponseDto {
    private LocalDate scheduleLogDate; // scheduleLogStart에서 날짜만 뽑아올예정

    private LocalTime workingTime; // end - start 하면 나올까 싶다.

    private long salary; // 그날 얼마의 급여를 주게되었는가? - 이건 좀 애매하긴한데 salarylog에서 꺼내야하나

    private int bonusAmount; // 그중에 얼마가 추가수당인가?
}
