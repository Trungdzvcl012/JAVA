package com.nhakhoa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final UserDetailsService userDetailsService;
    
    // Constructor injection - sử dụng CustomUserDetailsService
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
    
    // Custom Success Handler để xử lý redirect theo role
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String roleParam = request.getParameter("role");
            String targetUrl = "/xu-ly-dang-nhap-thanh-cong";
            
            if (roleParam != null && !roleParam.isEmpty()) {
                targetUrl += "?role=" + roleParam;
            } else {
                // Fallback: xác định role dựa trên authorities
                if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
                    targetUrl += "?role=DOCTOR";
                } else if (authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {
                    targetUrl += "?role=STAFF";
                } else {
                    targetUrl += "?role=PATIENT";
                }
            }
            
            response.sendRedirect(targetUrl);
        };
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(authz -> authz
                // CÁC URL CÔNG KHAI - ĐÃ BỔ SUNG
                .requestMatchers(
                    "/", 
                    "/dang-ky", 
                    "/dang-nhap", 
                    "/xu-ly-dang-nhap-thanh-cong",
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/webjars/**", 
                    "/h2-console/**",
                    "/dich-vu", 
                    "/bac-si", 
                    "/gioi-thieu",
                    "/dang-ky-nhan-vien", 
                    "/dang-ky-bac-si"
                ).permitAll()
                
                // Các URL yêu cầu đăng nhập
                .requestMatchers("/trang-chu", "/lich-hen/**", "/dat-lich/**").authenticated()
                
                // Các URL theo vai trò
                .requestMatchers("/benh-nhan/**").hasAnyRole("PATIENT", "ADMIN")
                .requestMatchers("/bac-si/**").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers("/nhan-vien/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/quan-tri/**").hasRole("ADMIN")
                
                // Mọi request khác đều yêu cầu đăng nhập
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/dang-nhap")
                .loginProcessingUrl("/dang-nhap")
                .successHandler(customAuthenticationSuccessHandler()) // Sử dụng custom success handler
                .failureUrl("/dang-nhap?error=true")
                .usernameParameter("email")
                .passwordParameter("matKhau")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/dang-xuat"))
                .logoutSuccessUrl("/dang-nhap?logout=true")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            );
        
        // Cho phép truy cập H2 console (chỉ cho môi trường dev)
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        http.headers(headers -> headers.frameOptions().disable());
        
        return http.build();
    }
}