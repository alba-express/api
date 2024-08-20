package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.SlaveRegistRequestDto;
import com.albaExpress.api.alba.dto.response.SlaveAddCountSlaveListResponseDto;
import com.albaExpress.api.alba.dto.response.SlaveAllSlaveListResponseDto;
import com.albaExpress.api.alba.dto.response.SlaveOneSlaveInfoResponseDto;
import com.albaExpress.api.alba.dto.response.SlaveSearchSlaveInfoResponseDto;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.repository.SlaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

//    public Slave updateEmployee(String slavePhoneNumber, SlaveRegistRequestDto dto) {
//
//        Optional<Slave> oneSlave = slaveRepository.findById(slavePhoneNumber);
//
//        return oneSlave;
//    }
}
