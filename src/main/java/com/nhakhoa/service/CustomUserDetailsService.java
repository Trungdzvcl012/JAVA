package com.nhakhoa.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.repository.NguoiDungRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại với email: " + email));

        if (!nguoiDung.isDaKichHoat()) {
            throw new UsernameNotFoundException("Tài khoản chưa được kích hoạt");
        }

        return new User(
                nguoiDung.getEmail(),
                nguoiDung.getMatKhau(),
                getAuthorities(nguoiDung)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(NguoiDung nguoiDung) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaiTro()));
    }
}