package com.albaExpress.api.alba.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlaveDto {
    private String id;
    private String slaveName;
    private String slavePosition;
    private String scheduleStart; // 출근 시간
    private String scheduleEnd;   // 퇴근 시간

    public SlaveDto(String id, String slaveName) {
        this.id = id;
        this.slaveName = slaveName;
    }
}
