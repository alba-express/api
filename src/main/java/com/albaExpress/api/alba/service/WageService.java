package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.entity.SalaryLog;
import com.albaExpress.api.alba.repository.SalaryLogRepository;
import com.albaExpress.api.alba.repository.ScheduleLogRepository;
import com.albaExpress.api.alba.repository.WageRepository;
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


    public int getSalaryLogInWorkplace(String workplaceId, YearMonth ym) {

        List<SalaryLog> logList = salaryLogRepository.getLogListByWorkplace(workplaceId);
        int salaryAmount = 0;

        for (SalaryLog salaryLog : logList) {

            if (salaryLog.getSalaryMonth().getYear() == ym.getYear() && salaryLog.getSalaryMonth().getMonthValue() == ym.getMonthValue()) {
                salaryAmount += salaryLog.getSalaryAmount();

            }
        }
        log.info("service에서 controller가기전 결과물: {}", salaryAmount);
        return salaryAmount;
    }
}
