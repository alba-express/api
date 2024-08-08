package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.BonusLog;
import com.albaExpress.api.alba.entity.QBonusLog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import static com.albaExpress.api.alba.entity.QBonusLog.bonusLog;

@RequiredArgsConstructor
@Repository
public class BonusLogRepositoryCustomImpl implements BonusLogRepositoryCustom{

    private final JPAQueryFactory factory;


}
