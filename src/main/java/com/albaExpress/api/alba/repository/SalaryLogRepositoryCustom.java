package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.dto.response.SalaryLogDetailResponseDto;
import com.albaExpress.api.alba.dto.response.SalaryLogSlaveResponseDto;
import com.albaExpress.api.alba.entity.SalaryLog;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface SalaryLogRepositoryCustom {

    public List<SalaryLogSlaveResponseDto> getLogListByWorkplace(String workplaceId, YearMonth ym);

    public SalaryLogDetailResponseDto getSalaryLogDetail(String slaveId, YearMonth ym);
}
