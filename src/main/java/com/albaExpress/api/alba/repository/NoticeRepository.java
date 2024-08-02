package com.albaExpress.api.alba.repository;

import com.albaExpress.api.alba.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface NoticeRepository extends JpaRepository<Notice, String>,
        NoticeRepositoryCustom {



}

