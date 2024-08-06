package com.albaExpress.api.alba.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@ToString(exclude = "workplaceList")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_master")
public class Master implements UserDetails {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "master_id")
    private String id;

    @Setter
    @Column(name = "master_email", nullable = false, unique = true)
    private String masterEmail;

    @Setter
    @Column(name = "master_password")
    private String masterPassword;

    @Setter
    @Column(name = "master_name")
    private String masterName;

    @Setter
    @Column(name = "email_verified")
    private boolean emailVerified;

    @Setter
    @Column(name = "master_created_at")
    private LocalDateTime masterCreatedAt;

    @Setter
    @Column(name = "master_retired")
    private LocalDateTime masterRetired;

    @OneToMany(mappedBy = "master", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Workplace> workplaceList = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_USER");
    }

    @Override
    public String getPassword() {
        return masterPassword;
    }

    @Override
    public String getUsername() {
        return masterEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return emailVerified;
    }
}
