package com.albaExpress.api.alba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wage")
@RequiredArgsConstructor
public class WageController {



    @GetMapping("/main")
    public ResponseEntity<?> wageMainGet() {



        return ResponseEntity.ok().body("wageMainGetBody");
    }

    @GetMapping("/info")
    public ResponseEntity<?> wageInfoGet() {


        return ResponseEntity.ok().body("wageInfoGetBody");
    }
}
