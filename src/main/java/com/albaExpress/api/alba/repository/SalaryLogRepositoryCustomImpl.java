package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.QSalaryLog;
import com.albaExpress.api.alba.entity.SalaryLog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.albaExpress.api.alba.entity.QSalaryLog.salaryLog;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SalaryLogRepositoryCustomImpl implements SalaryLogRepositoryCustom{

    private final JPAQueryFactory factory;
    @Override
    public List<SalaryLog> getLogListByWorkplace(String workplaceId) {

        List<SalaryLog> salaryLogList = factory.select(salaryLog)
                .from(salaryLog)
                .where(salaryLog.slave.workplace.id.eq(workplaceId))
                .fetch();

        return salaryLogList;
    }
}
