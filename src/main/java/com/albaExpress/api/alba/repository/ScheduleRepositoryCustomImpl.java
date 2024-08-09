package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.dto.response.SalaryLogSlaveResponseDto;
import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.entity.QSchedule;
import com.albaExpress.api.alba.entity.QSlave;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.albaExpress.api.alba.entity.QSchedule.*;
import static com.albaExpress.api.alba.entity.QSlave.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ScheduleRepositoryCustomImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory factory;

    public List<ScheduleSlaveResponseDto> findSlaveBySchedule(String workplaceId, LocalDate date, int dayOfWeek) {

        List<Tuple> results = factory
                .select(slave.id, slave.slaveName, slave.slavePosition,
                        schedule.id, schedule.scheduleDay, schedule.scheduleStart, schedule.scheduleEnd)
                .from(slave)
                .leftJoin(schedule)
                .on(slave.id.eq(schedule.slave.id))
                .where(slave.workplace.id.eq(workplaceId)
                        .and(schedule.scheduleDay.eq(dayOfWeek)))
                .fetch();

        List<ScheduleSlaveResponseDto> dtoList = results.stream()
                .map(tuple -> ScheduleSlaveResponseDto.builder()
                        .slaveId(tuple.get(slave.id))
                        .slaveName(tuple.get(slave.slaveName))
                        .slavePosition(tuple.get(slave.slavePosition))
                        .scheduleId(tuple.get(schedule.id))
                        .scheduleDay(tuple.get(schedule.scheduleDay))
                        .scheduleStart(tuple.get(schedule.scheduleStart))
                        .scheduleEnd(tuple.get(schedule.scheduleEnd))
                        .build())
                .collect(Collectors.toList());

        for (int i = 0; i < dtoList.size(); i++) {
            ScheduleSlaveResponseDto dto = dtoList.get(i);
            log.info("레포지토리 dto{}: {}", i, dto);
        }

        return dtoList;

    }

}
