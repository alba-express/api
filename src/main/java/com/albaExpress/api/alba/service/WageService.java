package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.BonusRequestDto;
import com.albaExpress.api.alba.dto.request.SalarySlaveRequestDto;
import com.albaExpress.api.alba.dto.response.*;
import com.albaExpress.api.alba.entity.*;
import com.albaExpress.api.alba.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.id.UUIDGenerator;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WageService {

    private final SlaveRepository slaveRepository;

    private final SalaryLogRepository salaryLogRepository;

    private final BonusLogRepository bonusLogRepository;// 뽀로레로쉐

    private final ScheduleRepository scheduleRepository;

    private final ExtraScheduleRepository extraScheduleRepository;

    private final WageRepository wageRepository;

    public SalaryLogWorkplaceResponseDto getSalaryLogInWorkplace(String workplaceId, YearMonth ym) {

        List<SalaryLogSlaveResponseDto> logList = salaryLogRepository.getLogListByWorkplace(workplaceId, ym);
        List<WorkingTimeDto> workingTimeDtoList = salaryLogRepository.calcWorkingTime(workplaceId, ym);

        Map<String, BenefitDto> benefitMap = new HashMap<>();

        for (WorkingTimeDto dto : workingTimeDtoList) {
            String key = dto.getWeek() + "-" + dto.getSlaveId();
            if (benefitMap.containsKey(key)) {
                BenefitDto existingBenefit = benefitMap.get(key);
                existingBenefit.setWorkingTime(existingBenefit.getWorkingTime() + dto.getWorkingTime());
            } else {
                benefitMap.put(key, new BenefitDto(dto.getWeek(), dto.getWorkingTime(), dto.getSlaveId()));
            }
        }
        List<BenefitDto> benefitList = new ArrayList<>(benefitMap.values());
        log.info("베니핏디티오리스트: {}", benefitList);

        for (BenefitDto dto : benefitList) {
            for (SalaryLogSlaveResponseDto log : logList) {
                if (dto.getWorkingTime() >= 15 && dto.getSlaveId().equals(log.getSlaveId())) {
                    switch (dto.getWeek()) {
                        case 1:
                            log.setFirst(true);
                            log.setFirstWorkingTime(dto.getWorkingTime());
                            break;
                        case 2:
                            log.setSecond(true);
                            log.setSecondWorkingTime(dto.getWorkingTime());
                            break;
                        case 3:
                            log.setThird(true);
                            log.setThirdWorkingTime(dto.getWorkingTime());
                            break;
                        case 4:
                            log.setFourth(true);
                            log.setFourthWorkingTime(dto.getWorkingTime());
                            break;
                        case 5:
                            if (getSundayOfWeek(ym.atEndOfMonth()).getMonthValue() != ym.getMonthValue()) {
                                break;
                            }
                            log.setFifth(true);
                            log.setFifthWorkingTime(dto.getWorkingTime());
                            break;
                    }
                }
            }
        }


        long salaryAmount = 0L;

        for (SalaryLogSlaveResponseDto salaryLog : logList) {
            if (salaryLog.isWageType()) {
                salaryAmount += salaryLog.getTotalAmount();
            } else {
                salaryAmount += salaryLog.getWage();
            }
            if (salaryLog.isFirst()) {
                salaryAmount += (long) (salaryLog.getFirstWorkingTime() * salaryLog.getWage() / 5);
            }
            if (salaryLog.isSecond()) {
                salaryAmount += (long) (salaryLog.getSecondWorkingTime() * salaryLog.getWage() / 5);
            }
            if (salaryLog.isThird()) {
                salaryAmount += (long) (salaryLog.getThirdWorkingTime() * salaryLog.getWage() / 5);
            }
            if (salaryLog.isFourth()) {
                salaryAmount += (long) (salaryLog.getFourthWorkingTime() * salaryLog.getWage() / 5);
            }
            if (salaryLog.isFifth()) {
                salaryAmount += (long) (salaryLog.getFifthWorkingTime() * salaryLog.getWage() / 5);
            }

        }
        log.info("service에서 controller가기전 결과물: {}", salaryAmount);
        return SalaryLogWorkplaceResponseDto.builder()
                .salaryAmount(salaryAmount)
                .logList(logList)
                .build();
    }

    private LocalDate getSundayOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    public SalaryLogDetailResponseDto forSlavePost(SalarySlaveRequestDto reqDto) {
        SalaryLogDetailResponseDto salaryLogDetail = salaryLogRepository.getSalaryLogDetail(reqDto.getSlaveId(), reqDto.getYm());

        salaryLogDetail.setTotalSalary(salaryLogDetail.getDtoList().stream()
                .map(SalaryScheduleResponseDto::getSalary)
                .reduce(0L, Long::sum));


        log.info("서비스에서 반환값확인: {}", salaryLogDetail);
        return salaryLogDetail;
    }

    public SalaryLogDetailResponseDto addBonusAndSalaryLog(BonusRequestDto reqDto) {

        SalaryLog salaryLog = salaryLogRepository.addBonus(reqDto);
        salaryLog.setSalaryAmount(salaryLog.getSalaryAmount() + reqDto.getAmount());
        log.info("서비스에서 샐러리로그: {}", salaryLog);
        salaryLogRepository.save(salaryLog);
        log.info("서비스에서 workDate 타입구경: {}", reqDto.getWorkDate());
        BonusLog byBonusDay = bonusLogRepository.findByBonusDayAndSlaveId(reqDto.getWorkDate(), reqDto.getSlaveId());
        log.info("서비스에서 보너스로그 잘 가져왔는지 : {}", byBonusDay);
        Slave slave = slaveRepository.findById(reqDto.getSlaveId()).orElseThrow();
        if (byBonusDay == null) {
            log.info("여기들어는 왔는지 밥은먹고 다니는지 걱정된다: {}", reqDto.getSlaveId());

            bonusLogRepository.save(
                    BonusLog.builder()
                            .bonusAmount(reqDto.getAmount())
                            .bonusDay(reqDto.getWorkDate())
                            .slave(slave)
                            .id(UUID.randomUUID().toString())
                            .build()
            );
        } else {
            byBonusDay.setBonusAmount(byBonusDay.getBonusAmount() + reqDto.getAmount());
            bonusLogRepository.save(byBonusDay);
        }
        SalaryLogDetailResponseDto salaryLogDetail = salaryLogRepository.getSalaryLogDetail(slave.getId(), YearMonth.of(reqDto.getWorkDate().getYear(), reqDto.getWorkDate().getMonth()));
        salaryLogDetail.setTotalSalary(salaryLogDetail.getDtoList().stream()
                .map(SalaryScheduleResponseDto::getSalary)
                .reduce(0L, Long::sum));
        return salaryLogDetail;

    }

    public void putSalaryLog(ScheduleLog save) throws Exception {
        LocalDateTime logStart = save.getScheduleLogStart();
        LocalDateTime logEnd = save.getScheduleLogEnd();
        LocalDate logDate = LocalDate.of(logStart.getYear(), logStart.getMonth(), logStart.getDayOfMonth());
        String slaveId = save.getSlave().getId();
        int dayOfWeek = logDate.getDayOfWeek().getValue() == 7 ? 0 : logDate.getDayOfWeek().getValue();
        ExtraSchedule extraSchedule = extraScheduleRepository.findByExtraScheduleDateAndSlaveId(logDate, slaveId);
        Schedule schedule = scheduleRepository.findBySlaveIdAndScheduleDay(slaveId, dayOfWeek).get(0);
        Wage wage = wageRepository.getWageBySlaveAndDate(slaveId, logDate);
        if (wage == null) {
            //wage 가 null 이거나 월급제라면 return 하기
            return;
        }

        if(!wage.isWageType()) {

            SalaryLog salaryLog = salaryLogRepository.getSalaryLog(slaveId, YearMonth.of(logDate.getYear(), logDate.getMonthValue()));
            if(salaryLog == null) {

                salaryLogRepository.save(
                        SalaryLog.builder()
                                .slave(save.getSlave())
                                .salaryMonth(logDate)
                                .salaryAmount(wage.getWageAmount())
                                .build()
                );
            } else {
                return;
            }
        }




        LocalTime start = LocalTime.of(logStart.getHour(), logStart.getMinute(), logStart.getSecond());
        LocalTime end = LocalTime.of(logEnd.getHour(), logEnd.getMinute(), logEnd.getSecond());
        log.info("엑스트라스케쥴이 있다고? :{}", extraSchedule);
        LocalTime scheduleStart = schedule.getScheduleStart();
        LocalTime scheduleEnd = schedule.getScheduleEnd();
        if (extraSchedule != null) {
            // 엑스트라 스케쥴이 있다면 ~~
            // 여기에 이후에 수빈누님이 하고나서 extraschedule의 개념이 잡히면 경우에 따라 다르게 세팅
            // ex)기본스케쥴이 15:00 ~ 16:00 이고 extra schedule이 16:00 ~ 17:00 일 경우
            if (scheduleStart == extraSchedule.getExtraScheduleEnd()) {
                scheduleStart = extraSchedule.getExtraScheduleStart();
            } else if (scheduleEnd == extraSchedule.getExtraScheduleStart()) {
                scheduleEnd = extraSchedule.getExtraScheduleEnd();
            }
            // ex)기본스케쥴이 15:00 ~ 16:00 이고 extra schedule이 15:00 ~ 17:00 일 경우
//            scheduleStart = extraSchedule.getExtraScheduleStart();
//            scheduleEnd = extraSchedule.getExtraScheduleEnd();
        }


        if((start.isAfter(scheduleEnd) && end.isAfter(scheduleEnd)) || (start.isBefore(scheduleStart) && end.isBefore(scheduleStart))) {
            return;
        }

        if (start.isBefore(scheduleStart)) {
            start = scheduleStart;
        }
        if (end.isAfter(scheduleEnd)) {
            end = scheduleEnd;
        }
        Duration duration = Duration.between(start, end);
        if (duration.isNegative()) {

            // 시간기록이 음수로 되었다면
            // -- 즉 출근시간 이전에 출근을 하고 출근시간 이전에 퇴근을 한 경우거나
            // -- 퇴근시간 이후에 출근을 하고 퇴근을 한 경우
            // 위의 두가지는 실제로 일어날 수는 없고 실제로 일어났다면 자정 이전에 출근을 하고 자정 이후에 퇴근을 한 경우
            // 즉 날짜가 변했을 경우에 일어날 수도 있을거라고 본다. 그렇다면 해결을 해야지
            duration = duration.plusHours(24);
        }
        double hour = ((double) duration.getSeconds()) / (double) 3600;
        long salary = (long) (wage.getWageAmount() * hour);
        log.info("기입될 샐러리 로그: {}", salary);



        salaryLogRepository.save(
                SalaryLog.builder()
                        .slave(save.getSlave())
                        .salaryMonth(logDate)
                        .salaryAmount(salary)
                        .build()
        );


    }
}
