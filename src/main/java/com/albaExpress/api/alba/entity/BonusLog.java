package com.albaExpress.api.alba.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(exclude = "")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_bonus_log")
public class BonusLog {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "slave_id")
    private String id;

    @Column(name = "bonus_amount")
    private int bonusAmount;

    @Column(name = "bonus_day")
    private LocalDateTime bonusDay;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slave_id")
    private Slave slave;


}