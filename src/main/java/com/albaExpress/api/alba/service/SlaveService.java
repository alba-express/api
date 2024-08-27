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

        Slave oneSlave = dto.dtoToSlaveEntity();

        slaveRepository.save(oneSlave);
        log.info("새로운 직원이 등록되었습니다: {}", dto.getSlavePhoneNumber());
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

    public boolean isPhoneNumberValid(String inputPhoneNumber, String workPlaceId) {

        // 해당 workplace에 속한 모든 직원의 전화번호 목록을 가져옴
        List<Slave> allSlaves = slaveRepository.findByWorkplace_id(workPlaceId);
        boolean isValid = allSlaves.stream().map(Slave::getSlavePhoneNumber).anyMatch(phoneNumber -> phoneNumber.equals(inputPhoneNumber));

        return isValid;
    }

    public boolean modifyIsPhoneNumberValid(String inputPhoneNumber, String workPlaceId, String slaveId) {

        // 해당 workplace에 속한 모든 직원의 전화번호 목록을 가져옴
        List<Slave> allSlaves = slaveRepository.findByWorkplace_id(workPlaceId);
        boolean isValid = allSlaves.stream()
                .filter(slave -> !slave.getId().equals(slaveId))
                .map(Slave::getSlavePhoneNumber).anyMatch(phoneNumber -> phoneNumber.equals(inputPhoneNumber));

        return isValid;
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
            prevSchedule.setScheduleEndDate(LocalDate.now());

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

        // 직원 정보를 조회
        Slave oneSlave = slaveRepository.findById(slaveId).orElseThrow(() -> new RuntimeException("직원을 찾을 수 없음"));

        // 근무정보와 출퇴근 기록 조회
        List<Schedule> oneSlaveScheduleList = scheduleRepository.findBySlaveId(slaveId);
        List<ScheduleLog> oneSlaveScheduleLogList = scheduleLogRepository.findBySlave_Id(slaveId);

        // 결과 리스트 초기화
        List<SlaveScheduleLogStatusResponseDto> statusList = new ArrayList<>();

        // 입사일부터 퇴사일(또는 현재)까지 반복
        LocalDate startDate = oneSlave.getSlaveCreatedAt().toLocalDate();
        LocalDateTime oneSlaveFiredDate = oneSlave.getSlaveFiredDate();
        LocalDate endDate = (oneSlaveFiredDate != null) ? oneSlaveFiredDate.toLocalDate().minusDays(1) : LocalDate.now();

        while (!startDate.isAfter(endDate)) {
            LocalDate currentDate = startDate;
            DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();
            int dayOfWeekAsInt = convertDayOfWeekToInt(currentDayOfWeek);

            // 현재 날짜의 요일과 일치하는 근무 정보를 찾음
            Optional<Schedule> findThisDateSchedule = oneSlaveScheduleList.stream()
                    .filter(schedule -> schedule.getScheduleDay() == dayOfWeekAsInt)
                    .findFirst();

            // 근무 정보가 없는 경우 다음 날짜로 넘어감
            if (!findThisDateSchedule.isPresent()) {
                startDate = startDate.plusDays(1);
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

                // 근무 시간과 출퇴근 시간을 비교하여 상태를 결정

                if (actualStartTime.isAfter(scheduleEnd) || actualEndTime.isBefore(scheduleStart)) {
                    // 출근 시간이 근무 종료 후이거나 퇴근 시간이 근무 시작 전이면 결근으로 간주
                    status = ScheduleLogStatus.ABSENT;
                } else if (actualStartTime.isAfter(scheduleStart)) {
                    status = ScheduleLogStatus.LATE;
                } else if (actualEndTime.isBefore(scheduleEnd)) {
                    status = ScheduleLogStatus.EARLYLEAVE;
                } else {
                    status = ScheduleLogStatus.NORMAL;
                }
            }

            // 결과 리스트에 추가
            statusList.add(new SlaveScheduleLogStatusResponseDto(
                    currentDate,
                    status,
                    scheduleStart,
                    scheduleEnd,
                    actualStartTime,
                    actualEndTime
            ));

            // 다음 날짜로 이동
            startDate = startDate.plusDays(1);
        }

        // 결과 리스트 반환
        return statusList;
    }
}
