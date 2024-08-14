package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.dto.request.ExtraScheduleRequestDto;
import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.entity.Schedule;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.albaExpress.api.alba.entity.QExtraSchedule.*;
import static com.albaExpress.api.alba.entity.QSchedule.*;
import static com.albaExpress.api.alba.entity.QSlave.*;
import static com.albaExpress.api.alba.entity.QWorkplace.*;

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
                        .and(schedule.scheduleDay.eq(dayOfWeek))
                        .and(schedule.scheduleUpdateDate.before(date).or(schedule.scheduleUpdateDate.eq(date)))
                        .and(schedule.scheduleEndDate.after(date).or(schedule.scheduleEndDate.isNull())))
                .orderBy(schedule.scheduleStart.asc())
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

    @Override
    public List<ExtraScheduleRequestDto> getExtraSchedule(String workplaceId, LocalDate date) {

        List<Tuple> results = factory
                .select(slave.id, slave.slaveName, slave.slavePosition,
                        extraSchedule.id, extraSchedule.extraScheduleDate,
                        extraSchedule.extraScheduleStart, extraSchedule.extraScheduleEnd, extraSchedule.slave)
                .from(slave)
                .leftJoin(extraSchedule)
                .on(slave.id.eq(extraSchedule.slave.id))
//                .leftJoin(schedule)
//                .on(slave.id.eq(schedule.slave.id))

                .where(slave.workplace.id.eq(workplaceId)
                        .and(extraSchedule.extraScheduleDate.eq(date))
//                        .and(extraSchedule.extraScheduleStart.notBetween(schedule.scheduleStart, schedule.scheduleEnd))
//                        .and(extraSchedule.extraScheduleEnd.notBetween(schedule.scheduleStart, schedule.scheduleEnd))
                        )

                .orderBy(extraSchedule.extraScheduleStart.asc())
                .fetch();

        List<ExtraScheduleRequestDto> dtoList = results.stream()
                .map(tuple -> ExtraScheduleRequestDto.builder()
                        .slaveId(tuple.get(slave.id))
                        .slaveName(tuple.get(slave.slaveName))
                        .slavePosition(tuple.get(slave.slavePosition))
                        .date(tuple.get(extraSchedule.extraScheduleDate))
                        .startTime(tuple.get(extraSchedule.extraScheduleStart))
                        .endTime(tuple.get(extraSchedule.extraScheduleEnd))
                        .build())
                .collect(Collectors.toList());

        for (int i = 0; i < dtoList.size(); i++) {
            ExtraScheduleRequestDto dto = dtoList.get(i);
            log.info("일정 추가 레포지토리 dto{}: {}", i, dto);
        }
        return dtoList;
    }

    @Override
    public List<ScheduleSlaveResponseDto> findSlaveByWorkplaceId(String workplaceId) {
        List<Tuple> tupleList = factory
                .select(slave.id, slave.slaveName, slave.slavePosition)
                .from(slave)
                .leftJoin(workplace)
                .on(slave.workplace.id.eq(workplace.id))
                .where(slave.workplace.id.eq(workplaceId))
                .fetch();

        List<ScheduleSlaveResponseDto> dtoList = tupleList.stream()
                .map(tuple -> ScheduleSlaveResponseDto.builder()
                        .slaveId(tuple.get(slave.id))
                        .slaveName(tuple.get(slave.slaveName))
                        .slavePosition(tuple.get(slave.slavePosition))
                        .build())
                .collect(Collectors.toList());

        for (int i = 0; i < dtoList.size(); i++) {
            ScheduleSlaveResponseDto dto = dtoList.get(i);
            log.info("직원 조회 레포지토리 dto{}: {}", i, dto);
        }
        return dtoList;
    }

    @Override
    public Schedule findByDateAndSlaveId(LocalDate date, String slaveId) {
        return null;
    }

}
