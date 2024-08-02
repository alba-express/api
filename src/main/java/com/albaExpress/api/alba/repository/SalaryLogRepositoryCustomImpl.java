package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.dto.response.SalaryLogSlaveResponseDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import static com.albaExpress.api.alba.entity.QSalaryLog.salaryLog;
import static com.albaExpress.api.alba.entity.QSlave.slave;
import static com.albaExpress.api.alba.entity.QWage.wage;


@Repository
@RequiredArgsConstructor
@Slf4j
public class SalaryLogRepositoryCustomImpl implements SalaryLogRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<SalaryLogSlaveResponseDto> getLogListByWorkplace(String workplaceId, YearMonth ym) {
        List<Tuple> results = factory
                .select(slave.id, slave.slaveName, slave.slavePosition, wage.wageAmount, wage.wageType, wage.wageInsurance, salaryLog.salaryAmount.sum())
                .from(slave)
                .leftJoin(slave.wageList, wage)
                .leftJoin(slave.salaryLogList, salaryLog)
                .where(slave.workplace.id.eq(workplaceId)
                        .and(wage.wageUpdateDate.loe(ym.atEndOfMonth()))
                        .and(wage.wageEndDate.goe(ym.atDay(1)).or(wage.wageEndDate.isNull()))
                        .and(salaryLog.salaryMonth.year().eq(ym.getYear()))
                        .and(salaryLog.salaryMonth.month().eq(ym.getMonthValue())))
                .groupBy(slave.id, wage.id)
                .fetch();

        List<SalaryLogSlaveResponseDto> dtoList = results.stream()
                .map(tuple -> new SalaryLogSlaveResponseDto(
                        tuple.get(slave.id),
                        tuple.get(slave.slaveName),
                        tuple.get(slave.slavePosition),
                        tuple.get(wage.wageAmount),
                        tuple.get(wage.wageType),
                        tuple.get(wage.wageInsurance),
                        tuple.get(salaryLog.salaryAmount.sum())
                ))
                .collect(Collectors.toList());
        for (int i = 0; i < dtoList.size(); i++) {
            SalaryLogSlaveResponseDto dto = dtoList.get(i);
            log.info("레포지토리 dto{}: {}", i, dto);
        }
        return dtoList;
    }
}
