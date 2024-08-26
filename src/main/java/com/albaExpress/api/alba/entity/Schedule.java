package com.albaExpress.api.alba.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_schedule")
public class Schedule {
    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "schedule_id")
    private String id;

    @Column(name = "schedule_type")
    private boolean scheduleType;

    @Column(name = "schedule_day")
    private int scheduleDay;

    @Setter
    @Column(name = "schedule_start")
    private LocalTime scheduleStart;

    @Setter
    @Column(name = "schedule_end")
    private LocalTime scheduleEnd;
    @Setter
    @Column(name = "schedule_update_date")
    private LocalDate scheduleUpdateDate;

    @Setter
    @Column(name = "schedule_end_date")
    private LocalDate scheduleEndDate;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slave_id")
    private Slave slave;
}