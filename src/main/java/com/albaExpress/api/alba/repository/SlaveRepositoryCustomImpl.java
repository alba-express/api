package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.QSlave;
import com.albaExpress.api.alba.entity.QWorkplace;
import com.albaExpress.api.alba.entity.Slave;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.albaExpress.api.alba.entity.QSlave.slave;
import static com.albaExpress.api.alba.entity.QWorkplace.workplace;

@Repository
@RequiredArgsConstructor
public class SlaveRepositoryCustomImpl implements SlaveRepositoryCustom{

    private final JPAQueryFactory factory;
    @Override
    public Slave getSlaveByPhoneNumber(String phoneNumber, String workplaceId) {

        return factory.select(slave)
                .from(slave)
                .where(slave.slavePhoneNumber.eq(phoneNumber).and(slave.workplace.id.eq(workplaceId)))
                .fetchOne();

    }

    public Slave getSlaveById(String id) {
        return factory.select(slave)
                .from(slave)
                .where(slave.id.eq(id))
                .fetchOne();
    }
}
