package com.albaExpress.api.alba.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SlaveDto {
    private String id;
    private String slaveName;
    private String slavePosition;
    private String scheduleStart; // 출근 시간
    private String scheduleEnd;   // 퇴근 시간
    private String status;        // 근무 상태 (출근 전, 근무 중, 퇴근)

    public SlaveDto(String id, String slaveName) {
        this.id = id;
        this.slaveName = slaveName;
    }

    public SlaveDto(String id, String slaveName, String slavePosition, String scheduleStart, String scheduleEnd, String status) {
        this.id = id;
        this.slaveName = slaveName;
        this.slavePosition = slavePosition;
        this.scheduleStart = scheduleStart;
        this.scheduleEnd = scheduleEnd;
        this.status = status;
    }
}
