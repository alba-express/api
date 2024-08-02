package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.dto.response.SalaryLogSlaveResponseDto;
import com.albaExpress.api.alba.entity.QSalaryLog;
import com.albaExpress.api.alba.entity.QSlave;
import com.albaExpress.api.alba.entity.QWorkplace;
import com.albaExpress.api.alba.entity.SalaryLog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.albaExpress.api.alba.entity.QSalaryLog.salaryLog;
import static com.albaExpress.api.alba.entity.QSlave.slave;
import static com.albaExpress.api.alba.entity.QWorkplace.workplace;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SalaryLogRepositoryCustomImpl implements SalaryLogRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<SalaryLogSlaveResponseDto> getLogListByWorkplace(String workplaceId) {

        List<SalaryLog> salaryLogList = factory
                .select(salaryLog)
                .from(salaryLog)
                .innerJoin(salaryLog.slave, slave)
                .where(slave.workplace.id.eq(workplaceId))
                .fetch();

        List<SalaryLogSlaveResponseDto> salaryLogSlaveResponseDtoList = salaryLogList
                .stream()
                .map(SalaryLog::toDto)
                .collect(Collectors.toList());
        log.info("repository에서 결과물: {}", salaryLogSlaveResponseDtoList);
        return salaryLogSlaveResponseDtoList;
    }
}
