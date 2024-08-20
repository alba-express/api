package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Notice;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.albaExpress.api.alba.entity.QNotice.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public Page<Notice> findNotices(String workplaceId, Pageable pageable) {

        List<Notice> noticeList = factory
                .selectFrom(notice)
                .where(notice.workplace.id.eq(workplaceId))
                .orderBy(notice.noticeCreatedAt.desc()) // 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 게시글 수
        Long count = Optional.ofNullable(
                factory
                .select(notice.count())
                .from(notice)
                .where(notice.workplace.id.eq(workplaceId))
                .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(noticeList, pageable, count);
    }




}
