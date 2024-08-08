package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.BonusRequestDto;
import com.albaExpress.api.alba.dto.request.SalaryAmountRequestDto;
import com.albaExpress.api.alba.dto.request.SalarySlaveRequestDto;
import com.albaExpress.api.alba.dto.response.SalaryLogDetailResponseDto;
import com.albaExpress.api.alba.dto.response.SalaryLogWorkplaceResponseDto;
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
        SalaryLogWorkplaceResponseDto resDto = wageService.getSalaryLogInWorkplace(reqDto.getWorkplaceId(), reqDto.getYm());

        return ResponseEntity.ok().body(resDto);
    }

    @PostMapping("/slave")
    public ResponseEntity<?> wageSlavePost(@RequestBody SalarySlaveRequestDto reqDto) {
        log.info("slave요청들어옴!");

        SalaryLogDetailResponseDto resDto = wageService.forSlavePost(reqDto);


        return ResponseEntity.ok().body(resDto);
    }
    @PostMapping("/bonus")
    public ResponseEntity<?> wageBonusPost(@RequestBody BonusRequestDto reqDto) {

        log.info("reqDto: {}", reqDto);

        SalaryLogDetailResponseDto resDto = wageService.addBonusAndSalaryLog(reqDto);

        log.info("이거 꼭 봐야함 : {}" , resDto);
        return ResponseEntity.ok().body(resDto);
    }
}
