package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.WorkplaceModifyDto;
import com.albaExpress.api.alba.dto.request.WorkplacePostDto;
import com.albaExpress.api.alba.dto.response.WorkplaceFindAllDto;
import com.albaExpress.api.alba.dto.response.WorkplaceListDto;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.entity.Workplace;
import com.albaExpress.api.alba.repository.MasterRepository;
import com.albaExpress.api.alba.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Transient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkplaceService {

    private final WorkplaceRepository workplaceRepository;
    private final MasterRepository masterRepository;

    // 사장 아이디로 등록된 사업장 전체 조회 중간처리
    public WorkplaceListDto findList(String masterId) {
        // Master 엔티티 조회
        Master master = masterRepository.findById(masterId).orElse(null);
        if (master == null) {
            throw new IllegalArgumentException("Invalid masterId: " + masterId);
        }

        // 사업장 정보 조회
        List<Workplace> workplaces = workplaceRepository.findByMaster(master);

        // Workplace 엔티티를 WorkplaceFindAllDto로 변환
        List<WorkplaceFindAllDto> workplaceDto = workplaces.stream()
                .map(w -> WorkplaceFindAllDto.builder()
                        .id(w.getId())
                        .workplaceName(w.getWorkplaceName())
                        .workplaceAddressCity(w.getWorkplaceAddressCity())
                        .workplaceAddressStreet(w.getWorkplaceAddressStreet())
                        .workplaceAddressDetail(w.getWorkplaceAddressDetail())
                        .workplaceCreatedAt(w.getWorkplaceCreatedAt())
                        .masterId(w.getMaster().getId())
                        .workplaceSize(w.isWorkplaceSize())
                        .build())
                .collect(Collectors.toList());

        // DTO를 포함한 WorkplaceListDto 반환
        return WorkplaceListDto.builder()
                .masterId(masterId)
                .workplaces(workplaceDto)
                .build();
    }

    // 사업장 등록 중간처리
    public Workplace register(WorkplacePostDto dto) {

        // Master 엔티티 조회
        Master master = masterRepository.findById(dto.getMasterId()).orElse(null);
        if (master == null) {
            throw new IllegalArgumentException("Invalid masterId: " + dto.getMasterId());
        }

        // DTO 엔터티로 변환
        Workplace w = dto.toEntity(master);

        // 로그인한 사장 정보 가져오기 - 사장 아이디를 통해 Master 엔터티 조회
        // 여기에 Master 정보를 가져오는 로직 추가 (masterRepository 사용)
//        Master master = masterRepository.findById(dto.getMasterId()).orElse(null);
//        w.setMaster(master);

        // 사업장 등록 저장 및 반환
        return workplaceRepository.save(w);
    }

    // 사업장 수정 중간처리
    public WorkplaceListDto modify(WorkplaceModifyDto dto) {
        // 수정하려는 사업장이 존재하는지 확인
        Workplace existingWorkplace = workplaceRepository.findById(dto.getId()).orElse(null);
        if (existingWorkplace == null) {
            throw new IllegalArgumentException("Invalid workplaceID: " + dto.getId());
        }

        // 기존 엔터티를 업데이트
        if (dto.getWorkplaceName() != null) {
            existingWorkplace.setWorkplaceName(dto.getWorkplaceName());
        }
        if(dto.getBusinessNo() != null) {
            existingWorkplace.setBusinessNo(dto.getBusinessNo());
        }
        if(dto.getWorkplaceAddressCity() != null) {
            existingWorkplace.setWorkplaceAddressCity(dto.getWorkplaceAddressCity());
        }
        if(dto.getWorkplaceAddressStreet() != null) {
            existingWorkplace.setWorkplaceAddressStreet(dto.getWorkplaceAddressStreet());
        }
        if(dto.getWorkplaceAddressDetail() != null) {
            existingWorkplace.setWorkplaceAddressDetail(dto.getWorkplaceAddressDetail());
        }
        if(dto.getWorkplacePassword() != null) {
            existingWorkplace.setWorkplacePassword(dto.getWorkplacePassword());
        }

        existingWorkplace.setWorkplaceSize(dto.isWorkplaceSize());

        // 사장 Master 는 변경하지 않는다는 가정으로 설정하지 않음

        // JpaRepository save - 새로운 insert, 기존 데이터 업데이트 update
        workplaceRepository.save(existingWorkplace);

        // 수정 후 전체 목록 반환 - 사장 아이디
        return findList(existingWorkplace.getMaster().getId());
    }

    // 사업장 삭제 중간처리
    public WorkplaceListDto delete(String id) {
        log.info("Removing workplaceId: {}", id);

        // 삭제하려는 사업장이 존재하는지 확인
        Workplace existingWorkplace = workplaceRepository.findById(id).orElse(null);
        if (existingWorkplace == null) {
            throw new IllegalArgumentException("Invalid workplaceID: " + id);
        }

        // 사장 아이디 찾기
        String masterId = existingWorkplace.getMaster().getId();
        log.info("사장 아이디: {}", masterId);

        // 찾은 사업장 아이디 삭제 후
        workplaceRepository.deleteById(id);

        // 사업장 삭제 성공 시 해당 사장의 사업장 목록 반환
        return findList(masterId);
    }
}
