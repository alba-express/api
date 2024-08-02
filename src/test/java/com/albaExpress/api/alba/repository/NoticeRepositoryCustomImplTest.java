package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Notice;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class NoticeRepositoryCustomImplTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    JPAQueryFactory factory;

    @Test
    @DisplayName("페이징 처리")
    void pagingTest() {
        //given
        String id = "1";
        Pageable page = PageRequest.of(0, 2);
        //when
        Page<Notice> pages = noticeRepository.findNotices(id, page);
        //then
        assertNotNull(pages);
        System.out.println("\n\n\n");

    }


}