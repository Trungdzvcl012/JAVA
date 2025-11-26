package com.nhakhoa.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.VaiTro;
import com.nhakhoa.repository.NguoiDungRepository;

@Service
public class NguoiDungService {
    
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public NguoiDungService(NguoiDungRepository nguoiDungRepository, PasswordEncoder passwordEncoder) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // PHƯƠNG THỨC ĐĂNG KÝ - SỬA LẠI ĐỂ ADMIN LUÔN ĐƯỢC KÍCH HOẠT
    public void dangKyNguoiDung(NguoiDung nguoiDung) {
        System.out.println("=== SERVICE: ĐĂNG KÝ NGƯỜI DÙNG ===");
        System.out.println("Email: " + nguoiDung.getEmail());
        System.out.println("Vai trò: " + nguoiDung.getVaiTro());
        
        // Mã hóa mật khẩu
        if (nguoiDung.getMatKhau() != null && !nguoiDung.getMatKhau().isEmpty()) {
            nguoiDung.setMatKhau(passwordEncoder.encode(nguoiDung.getMatKhau()));
        }
        
        // Xử lý trạng thái kích hoạt: ADMIN luôn được kích hoạt
        if (nguoiDung.getVaiTro() == VaiTro.ADMIN || nguoiDung.getVaiTro() == VaiTro.PATIENT) {
            nguoiDung.setDaKichHoat(true);
            System.out.println("=== " + nguoiDung.getVaiTro() + " - TỰ ĐỘNG KÍCH HOẠT ===");
        } else {
            nguoiDung.setDaKichHoat(false); // Bác sĩ/Nhân viên: chờ duyệt
            System.out.println("=== " + nguoiDung.getVaiTro() + " - CHỜ ADMIN DUYỆT ===");
        }
        
        // Đảm bảo thời gian tạo
        if (nguoiDung.getThoiGianTao() == null) {
            nguoiDung.setThoiGianTao(LocalDateTime.now());
        }
        
        // Lưu người dùng
        nguoiDungRepository.save(nguoiDung);
        
        System.out.println("=== ĐĂNG KÝ THÀNH CÔNG ===");
        System.out.println("Email: " + nguoiDung.getEmail());
        System.out.println("Vai trò: " + nguoiDung.getVaiTro());
        System.out.println("Trạng thái kích hoạt: " + nguoiDung.isDaKichHoat());
    }
    
    // THÊM PHƯƠNG THỨC CHO ADMIN
    public long countByVaiTro(String vaiTro) {
        return nguoiDungRepository.countByVaiTroString(vaiTro);
    }
    
    public long demBenhNhanMoiThang() {
        return nguoiDungRepository.countNewPatientsThisMonth();
    }
    
    public boolean existsByEmail(String email) {
        return nguoiDungRepository.existsByEmail(email);
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
            // Chỉ mã hóa nếu mật khẩu chưa được mã hóa
            if (!nguoiDung.getMatKhau().startsWith("$2a$")) {
                nguoiDung.setMatKhau(passwordEncoder.encode(nguoiDung.getMatKhau()));
            }
        }
        return nguoiDungRepository.save(nguoiDung);
    }
    
    public void deleteById(Long id) {
        nguoiDungRepository.deleteById(id);
    }
 
    
    public long countByVaiTro(VaiTro vaiTro) {
        return nguoiDungRepository.countByVaiTro(vaiTro);
    }
    
    public boolean kiemTraMatKhau(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public NguoiDung updateNguoiDung(NguoiDung nguoiDung) {
        return save(nguoiDung);
    }
    
    public NguoiDung createNguoiDung(NguoiDung nguoiDung) {
        return save(nguoiDung);
    }
    
    public List<NguoiDung> findByDaKichHoat(boolean daKichHoat) {
        return nguoiDungRepository.findByDaKichHoat(daKichHoat);
    }
    
    public List<NguoiDung> findByVaiTroAndDaKichHoat(VaiTro vaiTro, boolean daKichHoat) {
        return nguoiDungRepository.findByVaiTroAndDaKichHoat(vaiTro, daKichHoat);
    }
    
    // THÊM PHƯƠNG THỨC TOGGLE TRẠNG THÁI
    public void toggleTrangThai(Long id) {
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findById(id);
        if (nguoiDungOpt.isPresent()) {
            NguoiDung nguoiDung = nguoiDungOpt.get();
            nguoiDung.setDaKichHoat(!nguoiDung.isDaKichHoat());
            nguoiDungRepository.save(nguoiDung);
            System.out.println("=== TOGGLE TRẠNG THÁI: " + nguoiDung.getEmail() + " -> " + nguoiDung.isDaKichHoat() + " ===");
        }
    }
    
    public void kichHoatTaiKhoan(Long id) {
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findById(id);
        if (nguoiDungOpt.isPresent()) {
            NguoiDung nguoiDung = nguoiDungOpt.get();
            nguoiDung.setDaKichHoat(true);
            nguoiDungRepository.save(nguoiDung);
            System.out.println("=== ĐÃ KÍCH HOẠT TÀI KHOẢN: " + nguoiDung.getEmail() + " ===");
        }
    }
    
    public void voHieuHoaTaiKhoan(Long id) {
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findById(id);
        if (nguoiDungOpt.isPresent()) {
            NguoiDung nguoiDung = nguoiDungOpt.get();
            nguoiDung.setDaKichHoat(false);
            nguoiDungRepository.save(nguoiDung);
            System.out.println("=== ĐÃ VÔ HIỆU HÓA TÀI KHOẢN: " + nguoiDung.getEmail() + " ===");
        }
    }
}