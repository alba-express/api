package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.dto.response.WorkplaceFindAllDto;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkplaceRepository extends JpaRepository<Workplace, String> {


    // 사장 ID로 사업장 리스트 조회
    List<Workplace> findByMaster(Master master);

    boolean existsByBusinessNo(String businessNo);
}
