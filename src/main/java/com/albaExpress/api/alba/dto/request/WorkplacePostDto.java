package com.albaExpress.api.alba.dto.request;

import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.entity.Workplace;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.albaExpress.api.alba.entity.QMaster.master;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkplacePostDto {

    // 사업장 등록시 클라이언트에게 받야아 할 데이터만 DTO
    private String id;

    private String workplaceName;

    private String businessNo;

    private String workplaceAddressCity;

    private String workplaceAddressStreet;

    private String workplaceAddressDetail;

    private String workplacePassword;

    private boolean workplaceSize;

    private String masterId;

    // 엔터티로 변환하는 메서드
    public Workplace toEntity(Master master) {
        return Workplace.builder()
//                .id(this.id)
                .workplaceName(this.workplaceName)
                .businessNo(this.businessNo)
                .workplaceAddressCity(this.workplaceAddressCity)
                .workplaceAddressStreet(this.workplaceAddressStreet)
                .workplaceAddressDetail(this.workplaceAddressDetail)
                .workplacePassword(this.workplacePassword)
                .workplaceSize(this.workplaceSize)
                .master(master)
                .build();
    }
}
