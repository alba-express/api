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
}
