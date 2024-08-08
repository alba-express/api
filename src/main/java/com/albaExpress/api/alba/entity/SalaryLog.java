package com.albaExpress.api.alba.entity;
import com.albaExpress.api.alba.dto.response.SalaryLogSlaveResponseDto;
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
@Table(name = "tbl_salary_log")
public class SalaryLog {
    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "salary_id")
    private String id;

    @Column(name = "salary_month")
    private LocalDate salaryMonth;

    @Setter
    @Column(name = "salary_amount")
    private long salaryAmount;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slave_id")
    private Slave slave;

}