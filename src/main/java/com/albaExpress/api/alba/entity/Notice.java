package com.albaExpress.api.alba.entity;

import com.albaExpress.api.alba.dto.request.NoticeSaveDto;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_notice")
public class Notice {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "notice_no")
    private String id;

    @Column(name = "notice_title")
    private String noticeTitle;

    @Column(name = "notice_content")
    private String noticeContent;

    @Column(name = "notice_created_at")
    private LocalDateTime noticeCreatedAt;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workplace_id")
    private Workplace workplace;

    public void changeNotice(NoticeSaveDto dto) {
        this.noticeTitle = dto.getTitle();
        this.noticeContent = dto.getContent();
        this.noticeCreatedAt = LocalDateTime.now();
    }

}