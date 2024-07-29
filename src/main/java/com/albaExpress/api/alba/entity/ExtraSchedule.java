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
@Table(name = "tbl_extra_schedule")
public class ExtraSchedule {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "extra_schedule_id")
    private String id;

    @Column(name = "extra_schedule_date")
    private LocalDate extraScheduleDate;

    @Column(name = "extra_schedule_start")
    private LocalTime extraScheduleStart;

    @Column(name = "extra_schedule_end")
    private LocalTime extraScheduleEnd;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slave_id")
    private Slave slave;
}
