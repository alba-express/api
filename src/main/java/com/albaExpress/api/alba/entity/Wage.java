package com.albaExpress.api.alba.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_wage")
public class Wage {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "wage_id")
    private String id;

    @Column(name = "wage_insurance")
    private boolean wageInsurance;

    @Column(name = "wage_type")
    private boolean wageType;

    @Column(name = "wage_amount")
    private int wageAmount;

    @Column(name = "wage_update_date")
    private LocalDate wageUpdateDate;

    @Setter
    @Column(name = "wage_end_date")
    private LocalDate wageEndDate;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slave_id")
    private Slave slave;


}
