package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.entity.SalaryLog;
import com.albaExpress.api.alba.service.WageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wage")
@RequiredArgsConstructor
public class WageController {

    private final WageService wageService;


    @GetMapping("/workplace")
    public ResponseEntity<?> wageMainGet(@RequestParam String workplaceId) {

        List<SalaryLog> salaryLogList = wageService.getSalaryLogInWorkplace(workplaceId);


        return ResponseEntity.ok().body(salaryLogList);
    }
}
