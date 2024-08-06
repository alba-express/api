package com.albaExpress.api.alba.security;

import com.albaExpress.api.alba.entity.Master;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Master master;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(Master master, List<GrantedAuthority> authorities) {
        this.master = master;
        this.authorities = authorities;
    }

    public Master getMaster() {
        return master;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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
        return true;
    }
}
