package com.albaExpress.api.alba.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(exclude = {"bonusLogList", "wageList", "salaryLogList", "scheduleList", "extraScheduleList", "scheduleLogList"})
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_slave")
public class Slave {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "slave_id")
    private String id;

    @Setter
    @Column(name = "slave_name")
    private String slaveName;

    @Setter
    @Column(name = "slave_phone_number")
    private String slavePhoneNumber;

    @Setter
    @Column(name = "slave_birthday")
    private LocalDate slaveBirthday;

    @Column(name = "slave_created_at")
    private LocalDateTime slaveCreatedAt;

    @Setter
    @Column(name = "slave_fired_date")
    private LocalDateTime slaveFiredDate;

    @Setter
    @Column(name = "slave_position")
    private String slavePosition;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workplace_id")
    private Workplace workplace;

    @OneToMany(mappedBy = "slave", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<BonusLog> bonusLogList = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "slave", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Wage> wageList = new ArrayList<>();


    @OneToMany(mappedBy = "slave", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<SalaryLog> salaryLogList = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "slave", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Schedule> scheduleList = new ArrayList<>();

    @OneToMany(mappedBy = "slave", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ExtraSchedule> extraScheduleList = new ArrayList<>();

    @OneToMany(mappedBy = "slave", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ScheduleLog> scheduleLogList = new ArrayList<>();


}