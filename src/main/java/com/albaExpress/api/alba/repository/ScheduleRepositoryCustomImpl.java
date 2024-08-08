package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.entity.QSchedule;
import com.albaExpress.api.alba.entity.QSlave;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
                .leftJoin(slave.scheduleList, schedule)
                .where(slave.workplace.id.eq(workplaceId)
                        .and(schedule.scheduleDay.eq(dayOfWeek))
                        .and(schedule.scheduleUpdateDate.eq(date)))
                .fetch();

        // 슬레이브 ID로 그룹화된 스케줄 리스트 생성
        Map<String, List<ScheduleSlaveResponseDto.ScheduleDto>> groupedSchedules = results.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(slave.id),
                        Collectors.mapping(
                                tuple -> ScheduleSlaveResponseDto.ScheduleDto.builder()
                                        .scheduleId(tuple.get(schedule.id))
                                        .scheduleDay(tuple.get(schedule.scheduleDay))
                                        .scheduleStart(tuple.get(schedule.scheduleStart))
                                        .scheduleEnd(tuple.get(schedule.scheduleEnd))
                                        .build(),
                                Collectors.toList())
                ));

        return results.stream()
                .map(tuple -> ScheduleSlaveResponseDto.builder()
                        .slaveId(tuple.get(slave.id))
                        .slaveName(tuple.get(slave.slaveName))
                        .slavePosition(tuple.get(slave.slavePosition))
                        .schedules(groupedSchedules.get(schedule))
                        .build())
                .collect(Collectors.toList());


    }

}
