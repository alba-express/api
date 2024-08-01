package com.albaExpress.api.alba.repository;


import com.albaExpress.api.alba.entity.QSlave;
import com.albaExpress.api.alba.entity.QWage;
import com.albaExpress.api.alba.entity.QWorkplace;
import com.albaExpress.api.alba.entity.Slave;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

import static com.albaExpress.api.alba.entity.QSlave.*;
import static com.albaExpress.api.alba.entity.QWage.*;
import static com.albaExpress.api.alba.entity.QWorkplace.*;

@Repository
@RequiredArgsConstructor
public class WageRepositoryCustomImpl implements WageRepositoryCustom{


    private final JPAQueryFactory factory;
}
