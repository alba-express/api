package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.dto.request.BonusRequestDto;
import com.albaExpress.api.alba.dto.response.SalaryLogDetailResponseDto;
import com.albaExpress.api.alba.dto.response.SalaryLogSlaveResponseDto;
import com.albaExpress.api.alba.dto.response.SalaryScheduleResponseDto;
import com.albaExpress.api.alba.entity.SalaryLog;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.albaExpress.api.alba.entity.QBonusLog.bonusLog;
import static com.albaExpress.api.alba.entity.QSalaryLog.salaryLog;
import static com.albaExpress.api.alba.entity.QScheduleLog.scheduleLog;
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
                .map(tuple -> SalaryLogSlaveResponseDto.builder()
                        .slaveId(tuple.get(slave.id))
                        .slaveName(tuple.get(slave.slaveName))
                        .slavePosition(tuple.get(slave.slavePosition))
                        .wage(tuple.get(wage.wageAmount))
                        .wageType(tuple.get(wage.wageType))
                        .wageInsurance(tuple.get(wage.wageInsurance))
                        .totalAmount(tuple.get(salaryLog.salaryAmount.sum()))
                        .build()
                )
                .collect(Collectors.toList());

        for (int i = 0; i < dtoList.size(); i++) {
            SalaryLogSlaveResponseDto dto = dtoList.get(i);
            log.info("레포지토리 dto{}: {}", i, dto);
        }
        return dtoList;
    }


    @Override
    public SalaryLogDetailResponseDto getSalaryLogDetail(String slaveId, YearMonth ym) {
        // 1. Slave 정보 가져오기
        Tuple slaveInfo = factory
                .select(slave.id, slave.slaveName, wage.wageInsurance)
                .from(slave)
                .innerJoin(wage)
                .on(wage.slave.id.eq(slaveId)
                        .and(wage.wageUpdateDate.before(LocalDate.of(ym.getYear(), ym.getMonthValue(), 1))
                                .or(wage.wageUpdateDate.eq(LocalDate.of(ym.getYear(), ym.getMonthValue(), 1))))
                        .and(wage.wageEndDate.after(LocalDate.of(ym.getYear(), ym.getMonthValue(), 1))
                                .or(wage.wageEndDate.isNull())))
                .where(slave.id.eq(slaveId))
                .fetchOne();

        // 2. ScheduleLog 정보 가져오기 (scheduleLogEnd가 null이 아닌 경우만 포함)
        List<Tuple> scheduleLogResults = factory
                .select(scheduleLog.scheduleLogStart, scheduleLog.scheduleLogEnd, salaryLog.salaryAmount, bonusLog.bonusAmount)
                .from(scheduleLog)
                .leftJoin(salaryLog)
                .on(scheduleLog.slave.id.eq(salaryLog.slave.id)
                        .and(scheduleLog.scheduleLogStart.year().eq(salaryLog.salaryMonth.year()))
                        .and(scheduleLog.scheduleLogStart.month().eq(salaryLog.salaryMonth.month()))
                        .and(scheduleLog.scheduleLogStart.dayOfMonth().eq(salaryLog.salaryMonth.dayOfMonth())))
                .leftJoin(bonusLog)
                .on(bonusLog.slave.id.eq(scheduleLog.slave.id)
                        .and(scheduleLog.scheduleLogStart.year().eq(bonusLog.bonusDay.year()))
                        .and(scheduleLog.scheduleLogStart.month().eq(bonusLog.bonusDay.month()))
                        .and(scheduleLog.scheduleLogStart.dayOfMonth().eq(bonusLog.bonusDay.dayOfMonth())))
                .where(scheduleLog.slave.id.eq(slaveId)
                        .and(scheduleLog.scheduleLogStart.year().eq(ym.getYear()))
                        .and(scheduleLog.scheduleLogStart.month().eq(ym.getMonthValue()))
                        .and(scheduleLog.scheduleLogEnd.isNotNull())) // scheduleLogEnd가 null이 아닌 조건 추가
                .fetch();

        // 3. ScheduleLog 정보로 DTO 리스트 채우기
        List<SalaryScheduleResponseDto> dtoList = scheduleLogResults.stream()
                .map(tuple -> {
                    LocalDateTime start = tuple.get(scheduleLog.scheduleLogStart);
                    LocalDateTime end = tuple.get(scheduleLog.scheduleLogEnd);
                    long salary = tuple.get(salaryLog.salaryAmount) != null ? tuple.get(salaryLog.salaryAmount) : 0L;
                    int bonusAmount = tuple.get(bonusLog.bonusAmount) != null ? tuple.get(bonusLog.bonusAmount) : 0;

                    // Null 체크 제거 및 Duration 계산
                    LocalTime workingTime = null;
                    try {
                    Duration duration = Duration.between(start, end);
                        workingTime = LocalTime.of((int) duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());

                    } catch (DateTimeException e) {
                        workingTime = LocalTime.of(0,0,0);
                    }


                    return SalaryScheduleResponseDto.builder()
                            .scheduleLogDate(start.toLocalDate())
                            .workingTime(workingTime)
                            .salary(salary)
                            .bonusAmount(bonusAmount)
                            .build();
                })
                .collect(Collectors.toList());

        // 4. SalaryLogDetailResponseDto 반환
        return SalaryLogDetailResponseDto.builder()
                .slaveId(slaveInfo.get(slave.id))
                .slaveName(slaveInfo.get(slave.slaveName))
                .wageInsurance(slaveInfo.get(wage.wageInsurance))
                .dtoList(dtoList)
                .build();
    }

    public SalaryLog addBonus(BonusRequestDto reqDto) {
        return factory.selectFrom(salaryLog)
                .where(salaryLog.salaryMonth.eq(reqDto.getWorkDate()).and(salaryLog.slave.id.eq(reqDto.getSlaveId())))
                .fetchOne();
    }

}
