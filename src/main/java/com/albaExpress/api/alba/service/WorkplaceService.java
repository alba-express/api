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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public WorkplaceListDto findList(String masterId, int page, int size) {
        Master master = masterRepository.findById(masterId).orElse(null);
        if (master == null) {
            throw new IllegalArgumentException("Invalid masterId: " + masterId);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Workplace> workplacePage = workplaceRepository.findByMaster(master, pageable);

        List<WorkplaceFindAllDto> workplaceDto = workplacePage.getContent().stream()
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

        return WorkplaceListDto.builder()
                .workplaces(workplaceDto)
                .totalPages(workplacePage.getTotalPages())
                .currentPage(workplacePage.getNumber())
                .build();
    }

    // 사업장 개별조회 중간처리
    public Workplace getWorkplaceById(String id) {

        return workplaceRepository.findById(id).orElse(null);
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
    public WorkplaceListDto modify(String id, WorkplaceModifyDto dto) {
        Workplace existingWorkplace = workplaceRepository.findById(id).orElse(null);
        if (existingWorkplace == null) {
            throw new IllegalArgumentException("Invalid workplaceID: " + id);
        }

        // 엔티티 업데이트
        if (dto.getWorkplaceName() != null) {
            existingWorkplace.setWorkplaceName(dto.getWorkplaceName());
        }
        if (dto.getBusinessNo() != null) {
            existingWorkplace.setBusinessNo(dto.getBusinessNo());
        }
        if (dto.getWorkplaceAddressCity() != null) {
            existingWorkplace.setWorkplaceAddressCity(dto.getWorkplaceAddressCity());
        }
        if (dto.getWorkplaceAddressStreet() != null) {
            existingWorkplace.setWorkplaceAddressStreet(dto.getWorkplaceAddressStreet());
        }
        if (dto.getWorkplaceAddressDetail() != null) {
            existingWorkplace.setWorkplaceAddressDetail(dto.getWorkplaceAddressDetail());
        }
        if (dto.getWorkplacePassword() != null) {
            existingWorkplace.setWorkplacePassword(dto.getWorkplacePassword());
        }
        existingWorkplace.setWorkplaceSize(dto.isWorkplaceSize());

        workplaceRepository.save(existingWorkplace);

        return findList(existingWorkplace.getMaster().getId(), 0, 5); // 페이지 번호와 페이지 크기 기본값 설정
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
        return findList(masterId, 0, 5); // 페이지 번호, 크기 기본값 설정 !
    }

    // 사업장 간편비밀번호 인증 중간처리
    public boolean verifyPassword(String workplaceId, String inputPassword) {

        return workplaceRepository.findById(workplaceId)
                .map(workplace -> workplace.getWorkplacePassword().equals(inputPassword))
                .orElse(false);
    }

    // 사업장 등록번호 중복 체크 중간처리
    public boolean isBusinessNoDuplicateForMaster(String masterId, String businessNo) {
        // Master 엔티티 조회
        Master master = masterRepository.findById(masterId).orElse(null);
        if (master == null) {
            throw new IllegalArgumentException("Invalid masterId: " + masterId);
        }
        log.info("checking duplicate businessNo: {} masterId: {}", businessNo, masterId);

        // 특정 사장과 연결된 동일한 등록번호를 가진 사업장이 있는지 확인
        boolean exists = workplaceRepository.existsByMasterAndBusinessNo(master, businessNo);
        log.info("중복 체크 결과: {}", exists);

        return exists;
    }
}
