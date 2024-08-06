package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.Notice;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeListDto {

    private String id;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    public NoticeListDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getNoticeTitle();
        this.createdAt = notice.getNoticeCreatedAt();
    }
}
