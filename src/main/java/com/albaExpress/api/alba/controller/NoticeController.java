package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.NoticeSaveDto;
import com.albaExpress.api.alba.entity.Notice;
import com.albaExpress.api.alba.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/detail")
@RequiredArgsConstructor
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;

    // 전체 조회 요청
    @GetMapping("/notice")
    public ResponseEntity<?> getList() {

        return ResponseEntity.ok().body("공지사항 조회");
    }

    // 등록 요청
    @PostMapping("/notice-register")
    public ResponseEntity<?> post(@RequestBody NoticeSaveDto dto) {
        noticeService.saveNotice(dto, dto.getId());
        return ResponseEntity.ok().body("공지사항 등록");
    }

    // 수정 요청
    @PatchMapping
    public ResponseEntity<?> modify(@RequestBody NoticeSaveDto dto) {
        noticeService.modifyNotice(dto, dto.getId());
        return ResponseEntity.ok().body("공지사항 수정");
    }

    // 삭제 요청
    @DeleteMapping
    public ResponseEntity<?> delete(@PathVariable String id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.ok().body("공지사항 삭제");
    }




}
