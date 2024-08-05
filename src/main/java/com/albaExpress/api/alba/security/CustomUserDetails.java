package com.albaExpress.api.alba.security;

import com.albaExpress.api.alba.entity.Master;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Master master;

    public CustomUserDetails(Master master) {
        this.master = master;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // 권한 정보를 반환하지 않음
    }

    @Override
    public String getPassword() {
        return master.getMasterPassword();
    }

    @Override
    public String getUsername() {
        return master.getMasterEmail();
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
        return master.isEmailVerified();
    }
}
