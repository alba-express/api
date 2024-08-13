package com.albaExpress.api.alba.repository;


import com.albaExpress.api.alba.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static com.albaExpress.api.alba.entity.QSlave.*;
import static com.albaExpress.api.alba.entity.QWage.*;
import static com.albaExpress.api.alba.entity.QWorkplace.*;

@Repository
@RequiredArgsConstructor
public class WageRepositoryCustomImpl implements WageRepositoryCustom{


    private final JPAQueryFactory factory;

    public Wage getWageBySlaveAndDate(String slaveId, LocalDate date) {

        return factory.selectFrom(wage)
                .where(wage.slave.id.eq(slaveId)
                        .and(wage.wageUpdateDate.before(date).or(wage.wageUpdateDate.eq(date)))
                        .and(wage.wageEndDate.after(date).or(wage.wageEndDate.isNull())))
                .fetchOne();

    }
}
