package com.nhakhoa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.nhakhoa.service.NguoiDungService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final NguoiDungService nguoiDungService;
    
    // Sử dụng constructor injection với @Lazy
    public SecurityConfig(@Lazy NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService((UserDetailsService) nguoiDungService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/dang-ky", "/dang-nhap", "/css/**", "/js/**", "/images/**", 
                            "/dich-vu", "/bac-si", "/gioi-thieu").permitAll()
                .requestMatchers("/trang-chu", "/lich-hen/**").authenticated()
                .requestMatchers("/benh-nhan/**").hasAnyRole("PATIENT", "ADMIN")
                .requestMatchers("/bac-si/**").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers("/nhan-vien/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/quan-tri/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/dang-nhap")
                .loginProcessingUrl("/dang-nhap")
                .defaultSuccessUrl("/trang-chu", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/dang-xuat"))
                .logoutSuccessUrl("/")
                .permitAll()
            );
        
        return http.build();
    }
}