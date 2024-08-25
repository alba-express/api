package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.SlaveModifyRequestDto;
import com.albaExpress.api.alba.dto.request.SlaveModifyScheduleRequestDto;
import com.albaExpress.api.alba.dto.request.SlaveModifyWageRequestDto;
import com.albaExpress.api.alba.dto.request.SlaveRegistRequestDto;
import com.albaExpress.api.alba.dto.response.*;
import com.albaExpress.api.alba.entity.Schedule;
import com.albaExpress.api.alba.entity.ScheduleLog;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.entity.Wage;
import com.albaExpress.api.alba.repository.ScheduleLogRepository;
import com.albaExpress.api.alba.repository.ScheduleRepository;
import com.albaExpress.api.alba.repository.SlaveRepository;
import com.albaExpress.api.alba.repository.WageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SlaveService {

    @Autowired
    private final SlaveRepository slaveRepository;

    @Autowired
    private final WageRepository wageRepository;

    @Autowired
    private final ScheduleRepository scheduleRepository;

    @Autowired
    private final ScheduleLogRepository scheduleLogRepository;

    public void serviceRegistSlave(SlaveRegistRequestDto dto) {

        // 클라이언트에서 입력한 정보를 하나의 직원정보로 만들기
        Slave oneSlave = dto.dtoToSlaveEntity();

        List<Slave> slaveList = slaveRepository.findByWorkplace_id(dto.getWorkPlaceNumber());

        slaveRepository.save(oneSlave);
        log.info("새로운 직원이 등록되었습니다: {}", dto.getSlavePhoneNumber());

//        // 전화번호를 통해 DB에 직원 정보가 있는지 찾기
//        Optional<Slave> findSlave = slaveRepository.findBySlavePhoneNumber(dto.getSlavePhoneNumber());
//
//        // DB에 해당 전화번호를 가진 직원이 존재하는경우
//        if (findSlave.isPresent()) {
//            log.info("직원이 이미 존재합니다: {}", dto.getSlavePhoneNumber());
//
//            // DB에 해당 전화번호를 가진 직원이 존재하지 않는경우 --> 직원등록
//        } else {
//            slaveRepository.save(oneSlave);
//            log.info("새로운 직원이 등록되었습니다: {}", dto.getSlavePhoneNumber());
//        }
    }

    // 모든 근무중인 직원 목록 & 근무중인 직원 개수 조회하기
    public SlaveAddCountSlaveListResponseDto serviceGetAllActiveSlaveList() {

        // DB에 있는 모든직원 조회하기
        List<SlaveAllSlaveListResponseDto> activeSlaves =
                slaveRepository.findAll()
                        .stream()
                        .map(SlaveAllSlaveListResponseDto::new)
                        // 모든직원에서 근무중인 직원만 필터링하기 (퇴사일자가 없으면 근무중인 직원)
                        .filter(slave -> slave.getSlaveFiredDate() == null)
                        .collect(Collectors.toList());

        // 모든 근무중인 직원의 개수
        int totalSlaveCount = activeSlaves.size();

        return new SlaveAddCountSlaveListResponseDto(activeSlaves, totalSlaveCount);
    }

    // 모든 퇴사한 직원 목록 & 퇴사한 직원 개수 조회하기
    public SlaveAddCountSlaveListResponseDto serviceGetAllInactiveSlaveList() {

        // DB에 있는 모든직원 조회하기
        List<SlaveAllSlaveListResponseDto> inactiveSlaves =
                slaveRepository.findAll()
                        .stream()
                        .map(SlaveAllSlaveListResponseDto::new)
                        // 모든직원에서 퇴사한 직원만 필터링하기 (퇴사일자가 있으면 퇴사한 직원)
                        .filter(slave -> slave.getSlaveFiredDate() != null)
                        .collect(Collectors.toList());

        // 모든 퇴사한 직원의 개수
        int totalSlaveCount = inactiveSlaves.size();

        return new SlaveAddCountSlaveListResponseDto(inactiveSlaves, totalSlaveCount);
    }

    public Optional<SlaveOneSlaveInfoResponseDto> serviceGetOneSlave(String id) {

        // 클라이언트에서 보낸 직원id와 일치하는 id를 가진 직원 한 명 조회하기
        Optional<SlaveOneSlaveInfoResponseDto> selectSlave = slaveRepository.findById(id).map(SlaveOneSlaveInfoResponseDto::new);

        return selectSlave;
    }

    // 특정 사업장의 모든 직원 리스트 가져오기
    public List<SlaveAllSlaveListResponseDto> serviceGetAllSlaveList(String id) {

        List<Slave> allSlaves = slaveRepository.findByWorkplace_id(id);

        List<SlaveAllSlaveListResponseDto> allSlaveList = allSlaves.stream().map(SlaveAllSlaveListResponseDto::new).collect(Collectors.toList());

        return allSlaveList;
    }

    public List<SlaveSearchSlaveInfoResponseDto> searchSlaveByName(String slaveName, String id) {

        // 특정 사업장의 모든 직원 리스트 가져오기
        List<Slave> allSlaves = slaveRepository.findByWorkplace_id(id);

        // 사업장의 직원 중 이름이 일치하는 모든 직원 찾기
        return allSlaves.stream()
                .filter(slave -> slave.getSlaveName().equalsIgnoreCase(slaveName))
                .map(SlaveSearchSlaveInfoResponseDto::new)
                .collect(Collectors.toList());
    }

    public boolean isPhoneNumberValid(String slavePhoneNumber) {

        return slaveRepository.findBySlavePhoneNumber(slavePhoneNumber).isPresent();
    }

    // 해당 직원의 정보를 수정하기
    @Transactional
    public void serviceModifySlave(SlaveModifyRequestDto dto) {

        // 다음 달의 첫 번째 날 구하기 (종료일자)
        LocalDate firstDayOfNextMonth = LocalDate.now().plusMonths(1).withDayOfMonth(1);

        // 기존 직원정보를 slaveId 를 통해 조회하기
        Slave prevSlave = slaveRepository.findById(dto.getSlaveId()).orElseThrow(() -> new IllegalArgumentException("해당 직원이 없음 " + dto.getSlaveId()));

        // 기존 직원정보를 새로 받은 정보로 업데이트하기 (이름, 전화번호, 생일, 직책)
        prevSlave.setSlaveName(dto.getSlaveName());
        prevSlave.setSlavePhoneNumber(dto.getSlavePhoneNumber());
        prevSlave.setSlaveBirthday(dto.getSlaveBirthday());
        prevSlave.setSlavePosition(dto.getSlavePosition());

        //------------------------------------------

        // 현재 날짜
        LocalDate now = LocalDate.now();

        // 이전의 급여리스트 정보를 slaveId 를 통해 조회하기
        List<Wage> prevWages = wageRepository.findBySlaveId(dto.getSlaveId());

        for (Wage prevWage : prevWages) {
            // 이전 급여리스트 정보의 종료날짜를 오늘로 설정하기
            prevWage.setWageEndDate(firstDayOfNextMonth);

            // 이전 급여리스트 정보 저장하기 (종료날짜 업데이트)
            wageRepository.save(prevWage);
        }

        // 새로운 급여리스트 생성하기
        for (SlaveModifyWageRequestDto wageResponseDto : dto.getSlaveWageList()) {
            Wage newWage = wageResponseDto.dtoToWageEntity(prevSlave);

            wageRepository.save(newWage);
        }

        //------------------------------------------

        // 이전의 근무리스트 정보를 slaveId 를 통해 조회하기
        List<Schedule> prevSchedules = scheduleRepository.findBySlaveId(dto.getSlaveId());

        for (Schedule prevSchedule : prevSchedules) {
            // 이전 근무리스트 정보의 종료날짜를 다음달 1일로 설정하기
            prevSchedule.setScheduleEndDate(firstDayOfNextMonth);

            // 이전 근무리스트 정보 저장하기 (종료날짜 업데이트)
            scheduleRepository.save(prevSchedule);
        }

        // 새로운 근무리스트 생성하기
        for (SlaveModifyScheduleRequestDto scheduleDto : dto.getSlaveScheduleList()) {
            List<Schedule> newSchedule = scheduleDto.dtoToScheduleEntity(prevSlave);

            scheduleRepository.saveAll(newSchedule);
        }

        // 기존 직원정보를 업데이트한 직원정보로 변경저장하기
        slaveRepository.save(prevSlave);
    }

    @Transactional
    public void serviceFireSlave(String slaveId) {
        Slave firedSlave = slaveRepository.findById(slaveId)
                .orElseThrow(() -> new IllegalArgumentException("해당 직원이 없음: " + slaveId));

        // 탈퇴일자를 현재 시간으로 설정
        firedSlave.setSlaveFiredDate(LocalDateTime.now());
        slaveRepository.save(firedSlave);
    }

    // DayOfWeek를 int로 변환하는 유틸리티 함수
    private int convertDayOfWeekToInt(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case SUNDAY:
                return 0;
            case MONDAY:
                return 1;
            case TUESDAY:
                return 2;
            case WEDNESDAY:
                return 3;
            case THURSDAY:
                return 4;
            case FRIDAY:
                return 5;
            case SATURDAY:
                return 6;
            default:
                throw new IllegalArgumentException("Invalid DayOfWeek: " + dayOfWeek);
        }
    }

    public List<SlaveScheduleLogStatusResponseDto> serviceFindAllSlaveCommuteStatus(String slaveId) {

        // 직원아이디를 통해 직원 한 명을 조회하기
        Slave oneSlave = slaveRepository.findById(slaveId).orElseThrow(() -> new RuntimeException("직원을 찾을 수 없음"));

        // 입사일부터 퇴사일 이전까지의 출퇴근 기록을 조회
        LocalDate startDate = oneSlave.getSlaveCreatedAt().toLocalDate();
        LocalDateTime oneSlaveFiredDate = oneSlave.getSlaveFiredDate();
        LocalDate endDate = (oneSlaveFiredDate != null) ? oneSlaveFiredDate.toLocalDate().minusDays(1) : LocalDate.now();

        // 직원의 근무정보 조회하기
        List<Schedule> oneSlaveScheduleList = scheduleRepository.findBySlaveId(slaveId);

        // 직원의 출퇴근정보 조회하기
        List<ScheduleLog> oneSlaveScheduleLogList = scheduleLogRepository.findBySlave_Id(slaveId);

        // 결과 리스트 초기화
        List<SlaveScheduleLogStatusResponseDto> statusList = new ArrayList<>();

        // 입사일부터 퇴사일(또는 오늘)까지 반복
        while (!startDate.isAfter(endDate)) {

            // 현재 날짜를 람다식에서 사용하기 위해 로컬 변수에 저장
            LocalDate currentDate = startDate;

            // 현재 날짜의 요일을 가져오기
            DayOfWeek day = currentDate.getDayOfWeek();
            int dayOfWeekInt = convertDayOfWeekToInt(day);

            // 해당 날짜의 근무 정보 찾기
            Optional<Schedule> findThisDateSchedule = oneSlaveScheduleList.stream()
                    .filter(schedule -> schedule.getScheduleDay() == dayOfWeekInt && schedule.getScheduleEndDate() == null)
                    .findFirst();

            // 근무 정보가 없는 경우 다음 날짜로 넘어가기
            if (!findThisDateSchedule.isPresent()) {
                startDate = startDate.plusDays(1); // 날짜를 하루 증가시킴
                continue;
            }

            // 근무 정보 가져오기
            Schedule findSchedule = findThisDateSchedule.get();
            LocalTime scheduleStart = findSchedule.getScheduleStart();
            LocalTime scheduleEnd = findSchedule.getScheduleEnd();

            // 해당 날짜의 출퇴근 기록 찾기
            Optional<ScheduleLog> findThisDateScheduleLog = oneSlaveScheduleLogList.stream()
                    .filter(log -> log.getScheduleLogStart().toLocalDate().equals(currentDate))
                    .filter(log -> log.getScheduleLogEnd() != null)
                    .findFirst();

            // 출퇴근 기록에 따른 근무 현황 상태 설정
            ScheduleLogStatus status;
            LocalTime actualStartTime = null;
            LocalTime actualEndTime = null;

            if (!findThisDateScheduleLog.isPresent()) {
                status = ScheduleLogStatus.ABSENT;
            } else {
                ScheduleLog log = findThisDateScheduleLog.get();
                actualStartTime = log.getScheduleLogStart().toLocalTime();
                actualEndTime = log.getScheduleLogEnd().toLocalTime();

                if (actualStartTime.isAfter(scheduleStart)) {
                    status = ScheduleLogStatus.LATE;
                } else if (actualEndTime.isBefore(scheduleEnd)) {
                    status = ScheduleLogStatus.EARLYLEAVE;
                } else {
                    status = ScheduleLogStatus.NORMAL;
                }
            }

            // 결과 리스트에 추가 - 생성자에 모든 정보를 전달
            statusList.add(new SlaveScheduleLogStatusResponseDto(
                    currentDate,
                    status,
                    scheduleStart,
                    scheduleEnd,
                    actualStartTime,
                    actualEndTime
            ));

            // 날짜를 하루 증가시킴
            startDate = startDate.plusDays(1);
        }

        // 결과 리스트 반환
        return statusList;
    }
}
