package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.response.SalaryLogSlaveResponseDto;
import com.albaExpress.api.alba.dto.response.SalaryLogWorkplaceResponseDto;
import com.albaExpress.api.alba.entity.SalaryLog;
import com.albaExpress.api.alba.repository.SalaryLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WageService {

    private final SalaryLogRepository salaryLogRepository;


    public SalaryLogWorkplaceResponseDto getSalaryLogInWorkplace(String workplaceId, YearMonth ym) {

        List<SalaryLogSlaveResponseDto> logList = salaryLogRepository.getLogListByWorkplace(workplaceId);
        long salaryAmount = 0L;

        for (SalaryLogSlaveResponseDto salaryLog : logList) {

            if (salaryLog.getSalaryDate().getYear() == ym.getYear() && salaryLog.getSalaryDate().getMonthValue() == ym.getMonthValue()) {
                salaryAmount += salaryLog.getSalaryAmount();
//                log.info("반복문 내에서의 누적급여량: {}", salaryAmount);
            }
        }
        log.info("service에서 controller가기전 결과물: {}", salaryAmount);
        return SalaryLogWorkplaceResponseDto.builder()
                .salaryAmount(salaryAmount)
                .build();
    }
}
