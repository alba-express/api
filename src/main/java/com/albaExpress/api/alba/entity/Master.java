package com.albaExpress.api.alba.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(exclude = "workplaceList")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_master")
public class Master {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "master_id")
    private String id;

    @Column(name = "master_email", nullable = false, unique = true)
    private String masterEmail;

    @Column(name = "master_password")
    private String masterPassword;

    @Column(name = "master_name")
    private String masterName;

    @Setter
    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "master_created_at")
    private LocalDateTime masterCreatedAt;

    @Column(name = "master_retired")
    private LocalDateTime masterRetired;

    @OneToMany(mappedBy = "master", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Workplace> workplaceList = new ArrayList<>();
}
