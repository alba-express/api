package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.entity.SalaryLog;
import com.albaExpress.api.alba.repository.SalaryLogRepository;
import com.albaExpress.api.alba.repository.ScheduleLogRepository;
import com.albaExpress.api.alba.repository.WageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WageService {

    private SalaryLogRepository salaryLogRepository;


    public List<SalaryLog> getSalaryLogInWorkplace(String workplaceId) {

        List<SalaryLog> LogList = salaryLogRepository.getLogListByWorkplace(workplaceId);

        return LogList;
    }
}
