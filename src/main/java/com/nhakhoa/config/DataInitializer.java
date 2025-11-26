package com.nhakhoa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.VaiTro;
import com.nhakhoa.service.NguoiDungService;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private NguoiDungService nguoiDungService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Tạo tài khoản Bác sĩ nếu chưa tồn tại
        if (nguoiDungService.findByEmail("bacsi@nhakhoa.com").isEmpty()) {
            NguoiDung bacSi = new NguoiDung();
            bacSi.setHoTen("Bác sĩ Trần Văn C");
            bacSi.setEmail("bacsi@nhakhoa.com");
            bacSi.setMatKhau(passwordEncoder.encode("123456"));
            bacSi.setSoDienThoai("0901112222");
            bacSi.setDiaChi("123 Đường Bác Sĩ, Quận 1, TP.HCM");
            bacSi.setNgaySinh("15/08/1985");
            bacSi.setVaiTro(VaiTro.DOCTOR);
            bacSi.setDaKichHoat(true);
            nguoiDungService.save(bacSi);
            System.out.println("Đã tạo tài khoản Bác sĩ");
        }
        
        // Tạo tài khoản Nhân viên nếu chưa tồn tại
        if (nguoiDungService.findByEmail("nhanvien@nhakhoa.com").isEmpty()) {
            NguoiDung nhanVien = new NguoiDung();
            nhanVien.setHoTen("Nhân viên Lê Thị D");
            nhanVien.setEmail("nhanvien@nhakhoa.com");
            nhanVien.setMatKhau(passwordEncoder.encode("123456"));
            nhanVien.setSoDienThoai("0903334444");
            nhanVien.setDiaChi("456 Đường Nhân Viên, Quận 2, TP.HCM");
            nhanVien.setNgaySinh("20/11/1992");
            nhanVien.setVaiTro(VaiTro.STAFF);
            nhanVien.setDaKichHoat(true);
            nguoiDungService.save(nhanVien);
            System.out.println("Đã tạo tài khoản Nhân viên");
        }
        
        // Tạo tài khoản Admin nếu chưa tồn tại
        if (nguoiDungService.findByEmail("admin@nhakhoa.com").isEmpty()) {
            NguoiDung admin = new NguoiDung();
            admin.setHoTen("Quản trị viên");
            admin.setEmail("admin@nhakhoa.com");
            admin.setMatKhau(passwordEncoder.encode("123456"));
            admin.setSoDienThoai("0905556666");
            admin.setDiaChi("789 Đường Admin, Quận 3, TP.HCM");
            admin.setNgaySinh("10/03/1980");
            admin.setVaiTro(VaiTro.ADMIN);
            admin.setDaKichHoat(true);
            nguoiDungService.save(admin);
            System.out.println("Đã tạo tài khoản Admin");
        }
    }
}
