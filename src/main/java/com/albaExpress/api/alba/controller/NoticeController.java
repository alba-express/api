package com.albaExpress.api.alba.controller;

import com.albaExpress.api.alba.dto.request.NoticeSaveDto;
import com.albaExpress.api.alba.dto.response.NoticeListDto;
import com.albaExpress.api.alba.security.TokenProvider;
import com.albaExpress.api.alba.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/detail")
@RequiredArgsConstructor
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;

    // 전체 조회 요청
    @GetMapping("/notice")
    public ResponseEntity<Map<String, Object>> getList(@RequestParam String workplaceId,
                                                       @RequestParam(defaultValue = "1") int page) {

//        log.info("userInfo={}", userInfo);
        log.info("Fetching notices for userInfo={}, page={}", workplaceId, page);
        Map<String, Object> noticePage = noticeService.getNotices(workplaceId, page);
        log.info("Fetched notices: {}", noticePage);

        return ResponseEntity.ok().body(noticePage);
    }

    // 등록 요청
    @PostMapping("/notice-register")
    public ResponseEntity<?> post(@RequestBody NoticeSaveDto dto) {

        log.info("Registering notice with dto={}",  dto);

        try {
            noticeService.saveNotice(dto);
            log.info("Notice successfully registered: {}", dto);
            return ResponseEntity.ok().body(dto);
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 수정 요청
    @PatchMapping("/notice/{noticeId}")
    public ResponseEntity<?> modify(@RequestBody NoticeSaveDto dto, @PathVariable String noticeId) {
        log.info("Modifying notice with id={}, dto={}", noticeId, dto);
        noticeService.modifyNotice(dto, noticeId);
        return ResponseEntity.ok().body("Notice successfully modified");
    }

    // 삭제 요청
    @DeleteMapping("/notice/{noticeId}")
    public ResponseEntity<?> delete(@PathVariable String noticeId) {
        log.info("Deleting notice with id={}", noticeId);
        noticeService.deleteNotice(noticeId);
        log.info("noticeId : {}", noticeId);
        return ResponseEntity.ok().body("Notice successfully deleted");
    }


}
