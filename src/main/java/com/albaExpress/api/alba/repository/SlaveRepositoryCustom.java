package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Slave;

public interface SlaveRepositoryCustom {

    Slave getSlaveById(String id);
    Slave getSlaveByPhoneNumber(String phoneNumber, String workplaceId);


}