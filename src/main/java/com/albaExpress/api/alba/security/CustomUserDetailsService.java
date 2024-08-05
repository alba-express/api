package com.albaExpress.api.alba.security;

import com.albaExpress.api.alba.entity.Master;
import com.albaExpress.api.alba.repository.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MasterRepository masterRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Master master = masterRepository.findByMasterEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        return new CustomUserDetails(master, Collections.singletonList(authority));
    }
}
