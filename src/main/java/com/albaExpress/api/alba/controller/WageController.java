package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.SalaryAmountRequestDto;
import com.albaExpress.api.alba.service.WageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wage")
@RequiredArgsConstructor
@Slf4j
public class WageController {

    private final WageService wageService;

    @PostMapping("/workplace")
    public ResponseEntity<?> wageMainPost(@RequestBody SalaryAmountRequestDto reqDto) {
        log.info("요청들어옴!");
        int salaryAmount = wageService.getSalaryLogInWorkplace(reqDto.getWorkplaceId(), reqDto.getYm());

        return ResponseEntity.ok().body(salaryAmount);
    }
}
