package com.nhakhoa.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.VaiTro;
import com.nhakhoa.repository.NguoiDungRepository;

@Service
public class NguoiDungService implements UserDetailsService {
    
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    
    // Sử dụng constructor injection
    @Autowired
    public NguoiDungService(NguoiDungRepository nguoiDungRepository, PasswordEncoder passwordEncoder) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByEmail(email);
        if (nguoiDungOpt.isEmpty()) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email);
        }
        NguoiDung nguoiDung = nguoiDungOpt.get();
        return new User(nguoiDung.getEmail(), nguoiDung.getMatKhau(), getAuthorities(nguoiDung));
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(NguoiDung nguoiDung) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaiTro().name()));
    }
    
    public List<NguoiDung> findAll() {
        return nguoiDungRepository.findAll();
    }
    
    public Optional<NguoiDung> findById(Long id) {
        return nguoiDungRepository.findById(id);
    }
    
    public Optional<NguoiDung> findByEmail(String email) {
        return nguoiDungRepository.findByEmail(email);
    }
    
    public List<NguoiDung> findByVaiTro(VaiTro vaiTro) {
        return nguoiDungRepository.findByVaiTro(vaiTro);
    }
    
    public List<NguoiDung> findAllBacSiDangHoatDong() {
        return nguoiDungRepository.findAllBacSiDangHoatDong();
    }
    
    public NguoiDung save(NguoiDung nguoiDung) {
        if (nguoiDung.getMatKhau() != null && !nguoiDung.getMatKhau().isEmpty()) {
            nguoiDung.setMatKhau(passwordEncoder.encode(nguoiDung.getMatKhau()));
        }
        return nguoiDungRepository.save(nguoiDung);
    }
    
    public void deleteById(Long id) {
        nguoiDungRepository.deleteById(id);
    }
    
    public boolean existsByEmail(String email) {
        return nguoiDungRepository.existsByEmail(email);
    }
    
    public long countByVaiTro(VaiTro vaiTro) {
        return nguoiDungRepository.countByVaiTro(vaiTro);
    }
    
    public boolean kiemTraMatKhau(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}