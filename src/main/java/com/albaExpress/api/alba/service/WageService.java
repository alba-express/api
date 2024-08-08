package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.BonusRequestDto;
import com.albaExpress.api.alba.dto.request.SalarySlaveRequestDto;
import com.albaExpress.api.alba.dto.response.SalaryLogDetailResponseDto;
import com.albaExpress.api.alba.dto.response.SalaryLogSlaveResponseDto;
import com.albaExpress.api.alba.dto.response.SalaryLogWorkplaceResponseDto;
import com.albaExpress.api.alba.dto.response.SalaryScheduleResponseDto;
import com.albaExpress.api.alba.entity.BonusLog;
import com.albaExpress.api.alba.entity.SalaryLog;
import com.albaExpress.api.alba.entity.Slave;
import com.albaExpress.api.alba.repository.BonusLogRepository;
import com.albaExpress.api.alba.repository.SalaryLogRepository;
import com.albaExpress.api.alba.repository.SlaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WageService {

    private final SlaveRepository slaveRepository;

    private final SalaryLogRepository salaryLogRepository;

    private final BonusLogRepository bonusLogRepository;// 뽀로레로쉐

    public SalaryLogWorkplaceResponseDto getSalaryLogInWorkplace(String workplaceId, YearMonth ym) {

        List<SalaryLogSlaveResponseDto> logList = salaryLogRepository.getLogListByWorkplace(workplaceId, ym);
        long salaryAmount = 0L;

        for (SalaryLogSlaveResponseDto salaryLog : logList) {

            salaryAmount += salaryLog.getTotalAmount();
        }
        log.info("service에서 controller가기전 결과물: {}", salaryAmount);
        return SalaryLogWorkplaceResponseDto.builder()
                .salaryAmount(salaryAmount)
                .logList(logList)
                .build();
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
}
