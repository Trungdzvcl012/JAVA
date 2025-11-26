package com.nhakhoa.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhakhoa.model.LichHen;
import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.TrangThaiLichHen;
import com.nhakhoa.repository.LichHenRepository;

@Service
@Transactional
public class LichHenService {
    
    @Autowired
    private LichHenRepository lichHenRepository;
    
    @Transactional(readOnly = true)
    public List<LichHen> findAll() {
        return lichHenRepository.findAll(); // Sửa: xóa WithDetails()
    }
    
    @Transactional(readOnly = true)
    public Optional<LichHen> findById(Long id) {
        return lichHenRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<LichHen> findByNguoiDung(NguoiDung nguoiDung) {
        return lichHenRepository.findByNguoiDung(nguoiDung); // Sửa: xóa OrderByThoiGianHenDesc
    }
    
    @Transactional(readOnly = true)
    public List<LichHen> findByBacSi(NguoiDung bacSi) {
        return lichHenRepository.findByBacSi(bacSi); // Sửa: xóa OrderByThoiGianHenDesc
    }
    
    @Transactional(readOnly = true)
    public List<LichHen> findByTrangThai(TrangThaiLichHen trangThai) {
        return lichHenRepository.findByTrangThai(trangThai);
    }
    
    public LichHen save(LichHen lichHen) {
        if (lichHen.getThoiGianTao() == null) {
            lichHen.setThoiGianTao(LocalDateTime.now());
        }
        lichHen.setThoiGianCapNhat(LocalDateTime.now());
        return lichHenRepository.save(lichHen);
    }
    
    public void deleteById(Long id) {
        lichHenRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<LichHen> findLichHenSapToi() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusDays(7);
        // Sửa: tìm bằng cách filter
        return lichHenRepository.findAll().stream()
                .filter(lh -> lh.getThoiGianHen() != null && 
                             lh.getThoiGianHen().isAfter(now) && 
                             lh.getThoiGianHen().isBefore(end))
                .toList();
    }
    
    @Transactional(readOnly = true)
    public long demLichHenHoanThanhTheoNgay(LocalDate ngay) {
        // Sửa: tìm bằng cách filter
        return lichHenRepository.findAll().stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_HOAN_THANH &&
                             lh.getThoiGianHen() != null &&
                             lh.getThoiGianHen().toLocalDate().equals(ngay))
                .count();
    }
    
    // Thêm method để hủy lịch hẹn
    public boolean huyLichHen(Long id, String emailNguoiDung) {
        Optional<LichHen> lichHenOpt = lichHenRepository.findById(id);
        if (lichHenOpt.isPresent()) {
            LichHen lichHen = lichHenOpt.get();
            // Kiểm tra xem người dùng có quyền hủy lịch này không
            if (lichHen.getNguoiDung().getEmail().equals(emailNguoiDung) && 
                lichHen.getTrangThai() == TrangThaiLichHen.CHO_XAC_NHAN) {
                lichHen.setTrangThai(TrangThaiLichHen.DA_HUY);
                lichHen.setThoiGianCapNhat(LocalDateTime.now());
                lichHenRepository.save(lichHen);
                return true;
            }
        }
        return false;
    }
    
    // THÊM CÁC METHOD MỚI
    public boolean xacNhanLichHen(Long id, String email) {
        Optional<LichHen> lichHenOpt = lichHenRepository.findById(id);
        if (lichHenOpt.isPresent()) {
            LichHen lichHen = lichHenOpt.get();
            
            // Kiểm tra quyền: bác sĩ được phân công hoặc nhân viên
            if (lichHen.getBacSi().getEmail().equals(email) || email.equals("nhanvien")) {
                if (lichHen.getTrangThai() == TrangThaiLichHen.CHO_XAC_NHAN) {
                    lichHen.setTrangThai(TrangThaiLichHen.DA_XAC_NHAN);
                    lichHen.setThoiGianCapNhat(LocalDateTime.now());
                    lichHenRepository.save(lichHen);
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean capNhatTrangThai(Long id, TrangThaiLichHen trangThai) {
        Optional<LichHen> lichHenOpt = lichHenRepository.findById(id);
        if (lichHenOpt.isPresent()) {
            LichHen lichHen = lichHenOpt.get();
            lichHen.setTrangThai(trangThai);
            lichHen.setThoiGianCapNhat(LocalDateTime.now());
            lichHenRepository.save(lichHen);
            return true;
        }
        return false;
    }
    
    @Transactional(readOnly = true)
    public List<LichHen> findByBacSiEmail(String email) {
        // Sửa: tìm bằng cách filter
        return lichHenRepository.findAll().stream()
                .filter(lh -> lh.getBacSi() != null && lh.getBacSi().getEmail().equals(email))
                .toList();
    }
}
