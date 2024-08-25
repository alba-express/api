package com.albaExpress.api.alba.dto.response;

import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.entity.Workplace;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private String businessNo;
    // 사업장 규모 5인 이상 true이면 '5인 이상 사업장' 찍어두면 좋을거 같아서 보류 😬
    private boolean workplaceSize;
    private List<WorkplaceFindAllDto> workplaces;

    private int totalPages;
    private int currentPage;

    public WorkplaceListDto(Page<Workplace> workplacePage) {
        this.workplaces = workplacePage.getContent().stream()
                .map(w -> WorkplaceFindAllDto.builder()
                        .id(w.getId())
                        .businessNo(w.getBusinessNo())
                        .workplaceName(w.getWorkplaceName())
                        .workplaceAddressCity(w.getWorkplaceAddressCity())
                        .workplaceAddressStreet(w.getWorkplaceAddressStreet())
                        .workplaceAddressDetail(w.getWorkplaceAddressDetail())
                        .workplaceCreatedAt(w.getWorkplaceCreatedAt())
                        .masterId(w.getMaster().getId())
                        .build())
                .collect(Collectors.toList());
        this.totalPages = workplacePage.getTotalPages();
        this.currentPage = workplacePage.getNumber() + 1; // 페이지 번호는 1부터 시작하도록 조정
    }
}
