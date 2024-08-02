package com.albaExpress.api.alba.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Setter
@Entity
@Table(name = "tbl_schedule_log")
public class ScheduleLog {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "schedule_log_id")
    private String id;


    @Column(name = "schedule_log_start")
    private LocalDateTime scheduleLogStart;

    @Column(name = "schedule_log_end")
    private LocalDateTime scheduleLogEnd;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slave_id")
    @JsonIgnore
    private Slave slave;

}