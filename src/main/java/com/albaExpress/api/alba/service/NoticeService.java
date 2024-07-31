package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.NoticeSaveDto;
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
    public void saveNotice(NoticeSaveDto dto, Notice notice) {
        Notice newNotice = dto.toEntity();
        noticeRepository.save(newNotice);
    }

    // 수정
    public void modifyNotice(NoticeSaveDto dto, String id) {
        Notice foundNotice = noticeRepository.findById(id).orElseThrow();
        foundNotice.changeNotice(dto);
        noticeRepository.save(foundNotice);
    }

    // 삭제
    public void deleteNotice(String id) {
        noticeRepository.deleteById(id);
    }
}
