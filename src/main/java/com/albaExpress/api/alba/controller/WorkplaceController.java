package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.WorkplacePostDto;
import com.albaExpress.api.alba.dto.response.WorkplaceListDto;
import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.entity.Workplace;
import com.albaExpress.api.alba.service.WorkplaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workplace")
@Slf4j
@RequiredArgsConstructor
public class WorkplaceController {

    private final WorkplaceService workplaceService;

    // 사업장 전체 조회
    @GetMapping("/list/{masterId}")
    public ResponseEntity<?> workplaceList(@PathVariable("masterId") String masterId) {
        log.info("/workplace/list/{} : GET", masterId);

        // 특정 사업장 정보 조회하기 위해 사장 아이디 조회 필요 !


        WorkplaceListDto workplaceList = workplaceService.findList(masterId);

        return ResponseEntity.ok().body(workplaceList);
    }

    // 사업장 등록
    @PostMapping("/register")
    public ResponseEntity<?> workplaceSave(@Validated @RequestBody WorkplacePostDto dto) {
        log.info("/workplace/register : POST");
        log.debug("parameter - {}", dto);

        // 사업장 등록 - service
        Workplace registeredWorkplace = workplaceService.register(dto);

        // 등록 성공 시 등록된 사업장 정보 반환
        if (registeredWorkplace != null) {
            log.info("Workplace successfully registered");
            WorkplacePostDto workplaceListDto = WorkplacePostDto.builder()
//                    .id(registeredWorkplace.getId())
                    .workplaceName(registeredWorkplace.getWorkplaceName())
                    .businessNo(registeredWorkplace.getBusinessNo())
                    .workplaceAddressCity(registeredWorkplace.getWorkplaceAddressCity())
                    .workplaceAddressStreet(registeredWorkplace.getWorkplaceAddressStreet())
                    .workplaceAddressDetail(registeredWorkplace.getWorkplaceAddressDetail())
                    .workplacePassword(registeredWorkplace.getWorkplacePassword())
                    .workplaceSize(registeredWorkplace.isWorkplaceSize())
//                    .workplaceCreatedAt(registeredWorkplace.getWorkplaceCreatedAt())
                    .masterId(String.valueOf(registeredWorkplace.getMaster())) // Optional: 수정 필요
                    .build();
            return ResponseEntity.ok(workplaceListDto);
        } else {
            log.warn("Failed to register workplace");
            return ResponseEntity.internalServerError().body("Failed to register workplace");
        }
    }

    // 사업장 수정
    @PostMapping("/modify")
    public ResponseEntity<?> workplaceUpdate() {

        return ResponseEntity.ok().body("수정");
    }

    // 사업장 삭제
    @DeleteMapping("/delete/{mid}")
    public ResponseEntity<?> workplaceDelete() {

        return ResponseEntity.ok().body("삭제");
    }
}
