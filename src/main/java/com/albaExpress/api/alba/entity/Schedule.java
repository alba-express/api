package com.albaExpress.api.alba.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_schedule_log")
public class Schedule {
    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "schedule_id")
    private String id;
    @Column(name = "schedule_day")
    private int scheduleDay;

    @Column(name = "schedule_start")
    private LocalDateTime scheduleStart;
    @Column(name = "schedule_end")
    private LocalDateTime scheduleEnd;
    @Column(name = "schedule_update_date")
    private LocalDateTime scheduleUpdateDate;
    @Column(name = "schedule_end_date")
    private LocalDateTime scheduleEndDate;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slave_id")
    private Slave slave;
}