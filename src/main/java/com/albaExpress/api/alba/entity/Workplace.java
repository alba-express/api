package com.albaExpress.api.alba.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(exclude = {"slaveList", "noticeList"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_workplace")
public class Workplace {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "workplace_id", nullable = false)
    @Setter
    private String id;

    @Column(name = "workplace_name", nullable = false)
    private String workplaceName;

    @Column(name = "business_no", unique = true)
    private String businessNo;    // 사업자 등록번호

    @Column(name = "workplace_address_city")
    private String workplaceAddressCity; // 주소(시, 군)

    @Column(name = "workplace_address_street")
    private String workplaceAddressStreet; // 주소(도로명)

    @Column(name = "workplace_address_detail")
    private String workplaceAddressDetail;  // 상세주소

    @Column(name = "workplace_password", nullable = false)
    private String workplacePassword;   // 간편비밀번호

    @Column(name = "workplace_size", nullable = false)
    private boolean workplaceSize;    // 사업장규모 (5인 이상 유무)

    @Column(name = "workplace_created_at", nullable = false, updatable = false)
    private LocalDateTime workplaceCreatedAt;  // 업장 등록일

    @PrePersist
    protected void onCreate() {
        workplaceCreatedAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private Master master;

    @OneToMany(mappedBy = "workplace", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Slave> slaveList = new ArrayList<>();

    @OneToMany(mappedBy = "workplace", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Notice> noticeList = new ArrayList<>();

    // 상호명과 주소만 넣어 사업장 정보 전체 조회하는 부분 생성자
    public Workplace(String id, String workplaceName, String workplaceAddressCity, String workplaceAddressStreet, String workplaceAddressDetail) {
        this.id = id;
        this.workplaceName = workplaceName;
        this.workplaceAddressCity = workplaceAddressCity;
        this.workplaceAddressStreet = workplaceAddressStreet;
        this.workplaceAddressDetail = workplaceAddressDetail;
    }

    // 사업장 등록 시 필요한 정보만 담는 생성자
    public Workplace(String id, String workplaceName, String businessNo, String workplaceAddressCity, String workplaceAddressStreet, String workplaceAddressDetail, String workplacePassword, boolean workplaceSize) {
        this.id = id;
        this.workplaceName = workplaceName;
        this.businessNo = businessNo;
        this.workplaceAddressCity = workplaceAddressCity;
        this.workplaceAddressStreet = workplaceAddressStreet;
        this.workplaceAddressDetail = workplaceAddressDetail;
        this.workplacePassword = workplacePassword;
        this.workplaceSize = workplaceSize;
    }
}
