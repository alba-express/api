package com.albaExpress.api.alba.service;

import com.albaExpress.api.alba.dto.request.NoticeSaveDto;
import com.albaExpress.api.alba.entity.Notice;
import com.albaExpress.api.alba.entity.Workplace;
import com.albaExpress.api.alba.repository.NoticeRepository;
import com.albaExpress.api.alba.repository.NoticeRepositoryCustom;
import com.albaExpress.api.alba.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    private final WorkplaceRepository workplaceRepository;

    // 전체 조회
    public Page<Notice> getNotices(int pageNo, String id) {

        Pageable pageable = PageRequest.of(pageNo - 1, 5);

        Page<Notice> noticePage = noticeRepository.findNotices(id, pageable);

        List<Notice> noticeList = noticePage.getContent();

        log.info(noticeList.toString());


        return noticeRepository.findAll(pageable);
    }


    // 등록
    public void saveNotice(NoticeSaveDto dto, String id) {

        // 로그인한 회원 정보 조회
        Workplace workplace = workplaceRepository.findById(id).orElseThrow();

        Notice newNotice = dto.toEntity();
        newNotice.setWorkplace(workplace);

        Notice savedNotice = noticeRepository.save(newNotice);
        log.info("saved notice: {}", savedNotice);
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
