package com.albaExpress.api.alba.dto.request;

import com.albaExpress.api.alba.entity.Notice;
import com.albaExpress.api.alba.entity.Workplace;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeSaveDto {

    private String id;
    private String title;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;


    public Notice toEntity() {
        return Notice.builder()
                .id(this.id)
                .noticeTitle(this.title)
                .noticeContent(this.content)
                .noticeCreatedAt(this.createdAt)
                .build();
    }


}
