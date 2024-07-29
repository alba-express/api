package com.albaExpress.api.alba.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(exclude = "slaveList")
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
    @Column(name = "workplace_id")
    private String id;



    @Column(name = "workplace_name")
    private String workplaceName;

    @Column(name = "business_no")
    private String businessNo;

    @Column(name = "workplace_address_city")
    private String workplaceAddressCity;


    @Column(name = "workplace_address_street")
    private String workplaceAddressStreet;

    @Column(name = "workplace_address_detail")
    private String workplaceAddressDetail;

    @Column(name = "workplace_password")
    private String workplacePassword;

    @Column(name = "workplace_size")
    private boolean workplaceSize;

    @Column(name = "workplace_created_at")
    private LocalDateTime workplaceCreatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private Master master;

    @OneToMany(mappedBy = "workplace", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Slave> slaveList = new ArrayList<>();

    @OneToMany(mappedBy = "workplace", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Notice> noticeList = new ArrayList<>();




}
