package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/detail")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 해당 날짜 근무자 조회
    @GetMapping("/schedule-manage")
    public ResponseEntity<List<ScheduleSlaveResponseDto>> getSlaveBySchedule(@RequestParam String workplaceId,
                                                                            @RequestParam LocalDate date,
                                                                            @RequestParam int dayOfWeek ) {
        List<ScheduleSlaveResponseDto> scheduleData = scheduleService.findSlaveBySchedule(workplaceId, date, dayOfWeek);
        return ResponseEntity.ok(scheduleData);

    }

    /*
    api에서는 컨트롤러가 받아서 중간처리는 서비스에서 한다 치고 레포에서는 workplaceId를 통해 workplace에 접근하고
해당 workplace에 속해있는 slave에 조인을해서 slaveList 각각slave에서 schedule에 접근해서 payload에 입력받은 날짜를
통해서 해당하는 날에 근무가 있는지 확인해야 하는데? 여기서 중간처리를 하고 와야하는 이유가 있음

오늘날짜에 맞는 요일을 얻는 기능이 localdatetime에 있을건데 그것도아마 숫자로 나올거고 그걸 비교해서 추가로
scheduleenddate가 오늘날짜보다 미래이고, scheduleupdatedate가 오늘 날짜보다 과거인 schedule들을 꺼내와서
이거는 정상적으로 데이터관리가 이뤄져있다면 slave랑 1:1 일수밖에 없다고 보고
우리가 고른 날짜의 요일에 맞는 schedule을 결국 다시 컨트롤러가 받아다가
 responseentity에 담아서 return해주면 받아다가 페이지 렌더링 하면 될듯?
     */

}
