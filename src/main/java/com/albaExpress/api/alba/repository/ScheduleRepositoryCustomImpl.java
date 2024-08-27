package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.dto.request.ExtraScheduleRequestDto;
import com.albaExpress.api.alba.dto.response.ScheduleAndLogDto;
import com.albaExpress.api.alba.dto.response.ScheduleSlaveResponseDto;
import com.albaExpress.api.alba.entity.ExtraSchedule;
import com.albaExpress.api.alba.entity.Schedule;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.albaExpress.api.alba.entity.QExtraSchedule.*;
import static com.albaExpress.api.alba.entity.QSchedule.*;
import static com.albaExpress.api.alba.entity.QScheduleLog.scheduleLog;
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
                        .and(slave.slaveFiredDate.isNull())
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
                .where(slave.workplace.id.eq(workplaceId)
                        .and(slave.slaveFiredDate.isNull())
                        .and(extraSchedule.extraScheduleDate.eq(date))
                )
                .orderBy(extraSchedule.extraScheduleStart.asc())
                .fetch();

        List<ExtraScheduleRequestDto> dtoList = results.stream()
                .map(tuple -> ExtraScheduleRequestDto.builder()
                        .id(tuple.get(extraSchedule.id))
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
                .where(slave.workplace.id.eq(workplaceId)
                        .and(slave.slaveFiredDate.isNull()))
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

    // 기존 스케줄 가져오기
    @Override
    public Schedule findScheduleBySlaveId(String slaveId, LocalDate date) {

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        log.info("dayOfWeek: {}", dayOfWeek);

        return factory
                .select(schedule)
                .from(schedule)
                .where(schedule.slave.id.eq(slaveId)
                        .and(schedule.scheduleDay.eq(date.getDayOfWeek().getValue()))
                        .and(schedule.scheduleUpdateDate.before(LocalDate.now()).or(schedule.scheduleUpdateDate.eq(LocalDate.now())))
                        .and(schedule.scheduleEndDate.after(LocalDate.now()).or(schedule.scheduleEndDate.isNull())))
                .fetchOne();
    }


    // 지효씨의 추가메서드 컨플릭트시 추가만하면됨
    @Override
    public List<Schedule> findByScheduleDay(int day, String workplaceId, LocalDate date) {

//        List<Schedule> scheduleList = factory.select(schedule)
//                .from(schedule)
//                .where(schedule.scheduleDay.eq(day)
//                        .and(schedule.slave.workplace.id.eq(workplaceId))
//                        .and(schedule.scheduleEndDate.after(date).or(schedule.scheduleEndDate.isNull()))
//                        .and(schedule.scheduleUpdateDate.before(date).or(schedule.scheduleUpdateDate.eq(date)))
//                        )
//                .fetch();
        List<Schedule> scheduleList = factory.selectFrom(schedule)
                .where(schedule.slave.workplace.id.eq(workplaceId)
                        .and(schedule.scheduleDay.eq(day))
                        .and(schedule.scheduleEndDate.goe(date)
                                .or(schedule.scheduleEndDate.isNull()))
                        .and(schedule.scheduleUpdateDate.loe(date))
                )
                .fetch();

        List<ExtraSchedule> extraList = factory.select(extraSchedule)
                .from(extraSchedule)
                .where(extraSchedule.extraScheduleDate.eq(date)
                        .and(extraSchedule.slave.workplace.id.eq(workplaceId)))
                .fetch();
        List<Schedule> collect = extraList.stream().map(extra -> {

                    return Schedule.builder()
                            .slave(extra.getSlave())
                            .scheduleStart(extra.getExtraScheduleStart())
                            .scheduleEnd(extra.getExtraScheduleEnd())
                            .build();
                }
        ).collect(Collectors.toList());
//        List<Schedule> additionalSchedule = new ArrayList<>();
        for (Schedule extra : collect) {
            for (Schedule schedule : scheduleList) {
                if(schedule.getSlave().getId().equals(extra.getSlave().getId())) {
                    if(schedule.getScheduleStart().equals(extra.getScheduleEnd())) {
                        schedule.setScheduleStart(extra.getScheduleStart());
                    } else if(schedule.getScheduleEnd().equals(extra.getScheduleStart())) {
                        schedule.setScheduleEnd(extra.getScheduleEnd());
                    }
                }
            }
        }
        List<Schedule> collect1 = collect.stream().filter(extra -> {
            return scheduleList.stream().noneMatch(origin -> origin.getSlave().getId().equals(extra.getSlave().getId()));
        }).collect(Collectors.toList());
        scheduleList.addAll(collect1);

        for (Schedule schedule : scheduleList) {
            log.info("대체 뭐가 나오길래 그러는거야? :{}, {}", schedule.getSlave().getSlaveName(), schedule.getScheduleStart());
        }


        return scheduleList;
    }
    @Override
    public List<ScheduleAndLogDto> getScheduleAndScheduleLog(String workplaceId, LocalDate date) {
        int dayOfWeek = (date.getDayOfWeek().getValue() % 7);
        List<Tuple> list = factory.select(
                        scheduleLog.scheduleLogStart,
                        scheduleLog.scheduleLogEnd,
                        schedule.scheduleStart,
                        schedule.scheduleEnd,
                        schedule.slave.slaveName,
                        schedule.slave.slaveFiredDate, // 추가
                        schedule.slave.slavePosition)
                .from(schedule)
                .leftJoin(scheduleLog)
                .on(scheduleLog.slave.id.eq(schedule.slave.id)
                        .and(scheduleLog.scheduleLogStart.between(
                                LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0),
                                LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 23, 59, 59))
                        )
                )
                .where(schedule.slave.workplace.id.eq(workplaceId)
                        .and(schedule.scheduleDay.eq(dayOfWeek))
                        .and(schedule.scheduleUpdateDate.before(date).or(schedule.scheduleUpdateDate.eq(date)))
                        .and(schedule.scheduleEndDate.isNull().or(schedule.scheduleEndDate.after(date)))
                )
                .groupBy(
                        schedule.slave.slaveName,
                        schedule.slave.slavePosition,
                        schedule.slave.slaveFiredDate,
                        schedule.scheduleStart,
                        schedule.scheduleEnd,
                        scheduleLog.scheduleLogStart,
                        scheduleLog.scheduleLogEnd
                )
                .fetch();


        return list.stream().map(tuple -> {
            LocalDateTime logStartDateTime = tuple.get(scheduleLog.scheduleLogStart);
            LocalTime logStartTime = null;
            if(logStartDateTime != null) {
                logStartTime = logStartDateTime.toLocalTime();
            }
            LocalDateTime logEndDateTime = tuple.get(scheduleLog.scheduleLogEnd);
            LocalTime logEndTime = null;
            if(logEndDateTime != null) {
                logEndTime = logEndDateTime.toLocalTime();
            }
            return ScheduleAndLogDto.builder()
                    .scheduleLogStart(logStartTime)
                    .scheduleLogEnd(logEndTime)
                    .scheduleStart(tuple.get(schedule.scheduleStart))
                    .scheduleEnd(tuple.get(schedule.scheduleEnd))
                    .slaveName(tuple.get(schedule.slave.slaveName))
                    .slavePosition(tuple.get(schedule.slave.slavePosition))
                    .slaveFiredDate(tuple.get(schedule.slave.slaveFiredDate))
                    .build();
        }).collect(Collectors.toList());

    }
}
