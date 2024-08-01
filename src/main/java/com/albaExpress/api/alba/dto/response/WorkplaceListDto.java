package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.Master;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkplaceListDto {

    // 사업장 목록 조회를 위한 데이터만 DTO
    private String id;
    private String workplaceName;
    private String workplaceAddressCity;
    private String workplaceAddressStreet;
    private String workplaceAddressDetail;
    private LocalDateTime workplaceCreatedAt;
    private String masterId;

    // 사업장 규모 5인 이상 true이면 '5인 이상 사업장' 찍어두면 좋을거 같아서 보류 😬
//    private boolean workplaceSize;

    private List<WorkplaceFindAllDto> workplaces;

    // 나중에 로그인한 사장 정보 가져와 사업장 정보 조회하기 위해 필요하니 넣을거임
//    @Setter
//    private LoginUserInfoDto loginUserInfoDto;

    public WorkplaceListDto(WorkplaceFindAllDto w) {
        this.id = w.getId();
        this.workplaceName = w.getWorkplaceName();
        this.workplaceAddressCity = w.getWorkplaceAddressCity();
        this.workplaceAddressStreet = w.getWorkplaceAddressStreet();
        this.workplaceAddressDetail = w.getWorkplaceAddressDetail();
        this.workplaceCreatedAt = w.getWorkplaceCreatedAt();
        this.masterId = w.getMasterId();
//        this.workplaceSize = w.isWorkplaceSize();
    }
}
