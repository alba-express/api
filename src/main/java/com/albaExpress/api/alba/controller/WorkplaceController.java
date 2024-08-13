package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.WorkplaceModifyDto;
import com.albaExpress.api.alba.dto.request.WorkplacePostDto;
import com.albaExpress.api.alba.dto.response.WorkplaceListDto;
import com.albaExpress.api.alba.entity.Workplace;
import com.albaExpress.api.alba.service.WorkplaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/workplace")
@Slf4j
@RequiredArgsConstructor
public class WorkplaceController {

    private final WorkplaceService workplaceService;

    // 사업장 전체조회 - (사장 아이디)
    @GetMapping("/list/{masterId}")
    public ResponseEntity<?> workplaceList(@PathVariable("masterId") String masterId) {
        log.info("/workplace/list/{} : GET", masterId);

        // 특정 사업장 정보 조회하기 위해 사장 아이디 조회 필요 !


        WorkplaceListDto workplaceList = workplaceService.findList(masterId);

        return ResponseEntity.ok().body(workplaceList);
    }

    // 사업장 개별조회 - (사업장 아이디)
    @GetMapping("/{id}")
    public ResponseEntity<WorkplaceModifyDto> getWorkplaceById(@PathVariable("id") String id) {
        Workplace workplace = workplaceService.getWorkplaceById(id);

        if (workplace != null) {
            WorkplaceModifyDto response = WorkplaceModifyDto.builder()
                    .id(workplace.getId())
                    .businessNo(workplace.getBusinessNo())
                    .workplaceName(workplace.getWorkplaceName())
                    .workplaceAddressCity(workplace.getWorkplaceAddressCity())
                    .workplaceAddressStreet(workplace.getWorkplaceAddressStreet())
                    .workplaceAddressDetail(workplace.getWorkplaceAddressDetail())
                    .workplacePassword(workplace.getWorkplacePassword())
                    .workplaceSize(workplace.isWorkplaceSize())
//                    .workplaceCreatedAt(workplace.getWorkplaceCreatedAt())
                    .masterId(workplace.getMaster().getId())
                    .build();
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
                    .masterId(String.valueOf(registeredWorkplace.getMaster().getId())) // Optional: 수정 필요
                    .build();
            return ResponseEntity.ok(workplaceListDto);
        } else {
            log.warn("Failed to register workplace");
            return ResponseEntity.internalServerError().body("Failed to register workplace");
        }
    }

    // 사업장 등록번호 중복 확인
    @GetMapping("/checkBusinessNo/{businessNo}")
    public ResponseEntity<Map<String, Object>> checkBusinessNo(@PathVariable("businessNo") String businessNo) {
        log.info("/workplace/check-business-no/{} : GET", businessNo);

        boolean isDuplicate = workplaceService.isBusinessNoDuplicate(businessNo);

        Map<String, Object> response = new HashMap<>();
        response.put("exists", isDuplicate);

        return ResponseEntity.ok(response);
    }

    // 사업장 수정 - 사업장 아이디
    @PutMapping("/modify/{id}")
    public ResponseEntity<?> workplaceUpdate(@PathVariable("id") String id, @Valid @RequestBody WorkplaceModifyDto dto) {
        log.info("/workplace/modify/{} : PUT", id);
        log.debug("parameter - {}", dto);

        try {
            WorkplaceListDto workplaceListDto = workplaceService.modify(id, dto);
            return ResponseEntity.ok().body(workplaceListDto);
        } catch (IllegalArgumentException e) {
            log.warn("Error modifying workplace: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Workplace not found");
        }
    }

    // 사업장 삭제 - 사업장 아이디
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> workplaceDelete(@PathVariable("id") String id) {
        log.info("/workplace/delete/ : DELETE");
        log.info("workplaceId : {}", id);

        try {
            WorkplaceListDto removed = workplaceService.delete(id);
            return ResponseEntity.ok().body(removed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Workplace not found");
        }
    }

    // 사업장 간편비밀번호 인증 처리
    @PostMapping("/verify/{id}")
    public ResponseEntity<Map<String, Object>> verifyWorkplacePassword(@PathVariable("id") String id, @RequestBody Map<String, String> request) {
        String password = request.get("password");
        boolean isValid = workplaceService.verifyWorkplacePassword(id, password);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);

        return ResponseEntity.ok(response);
    }
}