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
        return lichHenRepository.findAllWithDetails();
    }
    
    @Transactional(readOnly = true)
    public Optional<LichHen> findById(Long id) {
        return lichHenRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<LichHen> findByNguoiDung(NguoiDung nguoiDung) {
        return lichHenRepository.findByNguoiDungOrderByThoiGianHenDesc(nguoiDung);
    }
    
    @Transactional(readOnly = true)
    public List<LichHen> findByBacSi(NguoiDung bacSi) {
        return lichHenRepository.findByBacSiOrderByThoiGianHenDesc(bacSi);
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
        return lichHenRepository.findUpcomingAppointments(now, end);
    }
    
    @Transactional(readOnly = true)
    public long demLichHenHoanThanhTheoNgay(LocalDate ngay) {
        return lichHenRepository.countCompletedAppointmentsByDate(ngay);
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
}
