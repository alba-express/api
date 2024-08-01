package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.SalaryLog;

import java.util.List;

public interface SalaryLogRepositoryCustom {

    public List<SalaryLog> getLogListByWorkplace(String workplaceId);
}
