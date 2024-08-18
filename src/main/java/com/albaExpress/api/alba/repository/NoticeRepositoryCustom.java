package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface NoticeRepositoryCustom {

    Page<Notice> findNotices(String workplaceId, Pageable pageable);

    Notice findLatestNotice(String workplaceId);

}
