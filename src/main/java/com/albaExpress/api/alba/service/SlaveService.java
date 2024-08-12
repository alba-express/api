package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.SlaveRegistRequestDto;
import com.albaExpress.api.alba.dto.response.SlaveActiveSlaveListResponseDto;
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

    public List<SlaveActiveSlaveListResponseDto> serviceGetAllActiveSlaveList() {

        // DB에 있는 모든직원 조회하기
        List<SlaveActiveSlaveListResponseDto> activeSlaveList =
                                                        slaveRepository.findAll()
                                                                            .stream()
                                                                            .map(SlaveActiveSlaveListResponseDto::new)
                // 모든직원에서 근무중인 직원만 필터링하기 (퇴사일자가 없으면 근무중인 직원)
                                                                            .filter(slave -> slave.getSlaveFiredDate() == null)
                                                                            .collect(Collectors.toList());

        return activeSlaveList;
    }

    public List<SlaveActiveSlaveListResponseDto> serviceGetAllInactiveSlaveList() {

        // DB에 있는 모든직원 조회하기
        List<SlaveActiveSlaveListResponseDto> inactiveSlaveList = slaveRepository.findAll()
                                                                                        .stream()
                                                                                        .map(SlaveActiveSlaveListResponseDto::new)
                // 모든직원에서 퇴사한 직원만 필터링하기 (퇴사일자가 있으면 퇴사한 직원)
                                                                                        .filter(slave -> slave.getSlaveFiredDate() != null)
                                                                                        .collect(Collectors.toList());
        return inactiveSlaveList;
    }
}
