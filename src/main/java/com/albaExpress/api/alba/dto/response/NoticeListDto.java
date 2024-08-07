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
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime date;

    public NoticeListDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getNoticeTitle();
        this.content = notice.getNoticeContent();
        this.date = notice.getNoticeCreatedAt();
    }
}
