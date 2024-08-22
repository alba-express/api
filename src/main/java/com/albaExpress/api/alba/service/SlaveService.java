package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.SlaveModifyRequestDto;
import com.albaExpress.api.alba.dto.request.SlaveModifyScheduleRequestDto;
import com.albaExpress.api.alba.dto.request.SlaveModifyWageRequestDto;
import com.albaExpress.api.alba.dto.request.SlaveRegistRequestDto;
import com.albaExpress.api.alba.dto.response.SlaveAddCountSlaveListResponseDto;
import com.albaExpress.api.alba.dto.response.SlaveAllSlaveListResponseDto;
import com.albaExpress.api.alba.dto.response.SlaveOneSlaveInfoResponseDto;
import com.albaExpress.api.alba.dto.response.SlaveSearchSlaveInfoResponseDto;
import com.albaExpress.api.alba.entity.Schedule;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.entity.Wage;
import com.albaExpress.api.alba.repository.ScheduleRepository;
import com.albaExpress.api.alba.repository.SlaveRepository;
import com.albaExpress.api.alba.repository.WageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public void serviceRegistSlave(SlaveRegistRequestDto dto) {

        // 클라이언트에서 입력한 정보를 하나의 직원정보로 만들기
        Slave oneSlave = dto.dtoToSlaveEntity();

        // 전화번호를 통해 DB에 직원 정보가 있는지 찾기
        Optional<Slave> findSlave = slaveRepository.findBySlavePhoneNumber(dto.getSlavePhoneNumber());

        // DB에 해당 전화번호를 가진 직원이 존재하는경우
        if (findSlave.isPresent()) {
            log.info("직원이 이미 존재합니다: {}", dto.getSlavePhoneNumber());

            // DB에 해당 전화번호를 가진 직원이 존재하지 않는경우 --> 직원등록
        } else {
            slaveRepository.save(oneSlave);
            log.info("새로운 직원이 등록되었습니다: {}", dto.getSlavePhoneNumber());
        }
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

        // 기존 직원정보를 slaveId 를 통해 조회하기
        Slave prevSlave = slaveRepository.findById(dto.getSlaveId()).orElseThrow(()-> new IllegalArgumentException("해당 직원이 없음 " + dto.getSlaveId()));

        // 기존 직원정보를 새로 받은 정보로 업데이트하기 (이름, 전화번호, 생일, 직책)
        prevSlave.setSlaveName(dto.getSlaveName());
        prevSlave.setSlavePhoneNumber(dto.getSlavePhoneNumber());
        prevSlave.setSlaveBirthday(dto.getSlaveBirthday());
        prevSlave.setSlavePosition(dto.getSlavePosition());

        //------------------------------------------

        // 이전의 근무리스트 정보를 slaveId 를 통해 조회하기
        List<Wage> prevWages = wageRepository.findBySlaveId(dto.getSlaveId());

        for (Wage prevWage : prevWages) {
            // 이전 근무리스트 정보의 종료날짜를 오늘로 설정하기
            prevWage.setWageEndDate(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1));

            // 이전 근무리스트 정보 저장하기 (종료날짜 업데이트)
            wageRepository.save(prevWage);
        }

        // 새로운 근무리스트 생성하기
        for (SlaveModifyScheduleRequestDto scheduleDto : dto.getSlaveScheduleList()) {
            List<Schedule> newSchedule = scheduleDto.dtoToScheduleEntity(prevSlave);

            scheduleRepository.saveAll(newSchedule);
        }

        //------------------------------------------

        // 이전의 근무리스트 정보를 slaveId 를 통해 조회하기
        List<Schedule> prevSchedules = scheduleRepository.findBySlaveId(dto.getSlaveId());

        for (Schedule prevSchedule : prevSchedules) {
            // 이전 근무리스트 정보의 종료날짜를 오늘로 설정하기
            prevSchedule.setScheduleEndDate(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1));

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
}
