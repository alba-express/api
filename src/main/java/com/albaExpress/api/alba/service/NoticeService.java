package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.entity.Notice;
import com.albaExpress.api.alba.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;


    // 등록
    public void saveNotice(Notice notice, String id) {
        noticeRepository.save(notice);
    }

    // 수정

    // 삭제
    public void deleteNotice(String id) {
        noticeRepository.deleteById(id);
    }
}
