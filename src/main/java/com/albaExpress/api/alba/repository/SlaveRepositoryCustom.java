package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Slave;

public interface SlaveRepositoryCustom {

    public Slave getSlaveById(String id);
    public Slave getSlaveByPhoneNumber(String phoneNumber, String workplaceId);
}