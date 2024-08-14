package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.Slave;
import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlaveOneSlaveInfoResponseDto {

    private String slaveId; // 직원 id

    private String slaveName; // 직원 이름

    public SlaveOneSlaveInfoResponseDto(Slave slave) {
        this.slaveId = slave.getId();
        this.slaveName = slave.getSlaveName();
    }
}
