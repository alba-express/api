package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WorkplaceRepository extends JpaRepository<Workplace, String> {


    // 사장 ID로 사업장 리스트 조회
    List<Workplace> findByMaster(Master master);

    boolean existsByMasterAndBusinessNo(Master master, String businessNo);

    Page<Workplace> findByMaster(Master master, Pageable pageable); // 사업장 전체조회 페이징을 위한 메서드 추가
}
