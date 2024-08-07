package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.service.SlaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/detail")
@RequiredArgsConstructor
public class SlaveController {

    private final SlaveService slaveService;
}
