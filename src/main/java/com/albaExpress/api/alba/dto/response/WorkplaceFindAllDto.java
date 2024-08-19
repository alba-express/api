package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.Master;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkplaceFindAllDto {

    // 사업장 목록 전체 조회할 때 필요한 데이터 DTO
    private String id;
    private String businessNo;
    private String workplaceName;
    private String workplaceAddressCity;
    private String workplaceAddressStreet;
    private String workplaceAddressDetail;
    private LocalDateTime workplaceCreatedAt;
    private String masterId;

    // 사업장 규모 5인 이상 true이면 '5인 이상 사업장' 찍어두면 좋을거 같아서 보류 😬
    private boolean workplaceSize;

}
