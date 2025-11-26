package com.nhakhoa.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhakhoa.model.HoaDon;
import com.nhakhoa.model.LichHen;
import com.nhakhoa.model.TrangThaiHoaDon;
import com.nhakhoa.repository.HoaDonRepository;
import com.nhakhoa.repository.LichHenRepository;

@Service
@Transactional
public class HoaDonService {

    @Autowired
    private HoaDonRepository hoaDonRepository;
    
    @Autowired
    private LichHenRepository lichHenRepository;

    // Tìm hóa đơn theo id - phiên bản cải tiến
    @Transactional(readOnly = true)
    public HoaDon findById(Long id) {
        Optional<HoaDon> hoaDonOpt = hoaDonRepository.findById(id);
        return hoaDonOpt.orElse(null);
    }
    
    // Phiên bản trả về Optional
    @Transactional(readOnly = true)
    public Optional<HoaDon> findByIdOptional(Long id) {
        return hoaDonRepository.findById(id);
    }

    // Lấy tất cả hóa đơn
    @Transactional(readOnly = true)
    public List<HoaDon> findAll() {
        return hoaDonRepository.findAll();
    }
    
    // Lấy tất cả hóa đơn theo trạng thái
    @Transactional(readOnly = true)
    public List<HoaDon> findByTrangThai(TrangThaiHoaDon trangThai) {
        return hoaDonRepository.findByTrangThai(trangThai);
    }
    
    // Tìm hóa đơn theo lịch hẹn
    @Transactional(readOnly = true)
    public Optional<HoaDon> findByLichHenId(Long lichHenId) {
        return hoaDonRepository.findAll().stream()
                .filter(hd -> hd.getLichHen() != null && hd.getLichHen().getId().equals(lichHenId))
                .findFirst();
    }

    // Lưu hoặc cập nhật hóa đơn
    public HoaDon save(HoaDon hoaDon) {
        if (hoaDon.getThoiGianTao() == null) {
            hoaDon.setThoiGianTao(LocalDateTime.now());
        }
        
        // Nếu chưa có tổng tiền, tính từ dịch vụ của lịch hẹn
        if (hoaDon.getTongTien() == null && hoaDon.getLichHen() != null) {
            BigDecimal giaDichVu = hoaDon.getLichHen().getDichVu().getGiaDichVu();
            hoaDon.setTongTien(giaDichVu);
        }
        
        return hoaDonRepository.save(hoaDon);
    }
    
    // Xóa hóa đơn
    public void deleteById(Long id) {
        hoaDonRepository.deleteById(id);
    }
    
    // Tạo hóa đơn từ lịch hẹn
    public HoaDon taoHoaDonTuLichHen(Long lichHenId) {
        Optional<LichHen> lichHenOpt = lichHenRepository.findById(lichHenId);
        if (lichHenOpt.isPresent()) {
            LichHen lichHen = lichHenOpt.get();
            
            // Kiểm tra xem đã có hóa đơn cho lịch hẹn này chưa
            Optional<HoaDon> existingHoaDon = findByLichHenId(lichHenId);
            if (existingHoaDon.isPresent()) {
                return existingHoaDon.get();
            }
            
            HoaDon hoaDon = new HoaDon();
            hoaDon.setLichHen(lichHen);
            hoaDon.setTongTien(lichHen.getDichVu().getGiaDichVu());
            hoaDon.setDaThanhToan(BigDecimal.ZERO);
            hoaDon.setTrangThai(TrangThaiHoaDon.CHUA_THANH_TOAN);
            hoaDon.setThoiGianTao(LocalDateTime.now());
            
            return hoaDonRepository.save(hoaDon);
        }
        return null;
    }
    
    // Phương thức thanh toán hóa đơn
    public boolean thanhToanHoaDon(Long id, String phuongThucThanhToan) {
        Optional<HoaDon> hoaDonOpt = hoaDonRepository.findById(id);
        if (hoaDonOpt.isPresent()) {
            HoaDon hoaDon = hoaDonOpt.get();
            
            if (hoaDon.getTrangThai() == TrangThaiHoaDon.CHUA_THANH_TOAN) {
                hoaDon.setDaThanhToan(hoaDon.getTongTien());
                hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
                hoaDon.setPhuongThucThanhToan(phuongThucThanhToan);
                hoaDon.setThoiGianThanhToan(LocalDateTime.now());
                
                // Tạo mã giao dịch
                String maGiaoDich = "GD" + System.currentTimeMillis();
                hoaDon.setMaGiaoDich(maGiaoDich);
                
                hoaDonRepository.save(hoaDon);
                return true;
            }
        }
        return false;
    }
    
    // Hủy hóa đơn
    public boolean huyHoaDon(Long id) {
        Optional<HoaDon> hoaDonOpt = hoaDonRepository.findById(id);
        if (hoaDonOpt.isPresent()) {
            HoaDon hoaDon = hoaDonOpt.get();
            
            if (hoaDon.getTrangThai() == TrangThaiHoaDon.CHUA_THANH_TOAN) {
                hoaDon.setTrangThai(TrangThaiHoaDon.DA_HUY);
                hoaDonRepository.save(hoaDon);
                return true;
            }
        }
        return false;
    }
    
    // Tính tổng doanh thu
    @Transactional(readOnly = true)
    public double tinhTongDoanhThu() {
        List<HoaDon> hoaDons = hoaDonRepository.findByTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        return hoaDons.stream()
                .mapToDouble(hd -> hd.getTongTien().doubleValue())
                .sum();
    }
}