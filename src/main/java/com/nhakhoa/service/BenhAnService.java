package com.nhakhoa.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhakhoa.model.BenhAn;
import com.nhakhoa.model.LichHen;
import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.repository.BenhAnRepository;
import com.nhakhoa.repository.LichHenRepository;

@Service
@Transactional
public class BenhAnService {
    
    @Autowired
    private BenhAnRepository benhAnRepository;
    
    @Autowired
    private LichHenRepository lichHenRepository;
    
    @Transactional(readOnly = true)
    public List<BenhAn> findAll() {
        return benhAnRepository.findAll(); // Sửa: xóa WithDetails()
    }
    
    @Transactional(readOnly = true)
    public Optional<BenhAn> findById(Long id) {
        return benhAnRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<BenhAn> findByBacSi(NguoiDung bacSi) {
        return benhAnRepository.findByBacSi(bacSi); // Sửa: xóa OrderByThoiGianTaoDesc
    }
    
    @Transactional(readOnly = true)
    public List<BenhAn> findByBenhNhan(NguoiDung benhNhan) {
        // Sửa: thay đổi cách query
        return benhAnRepository.findAll().stream()
                .filter(ba -> ba.getLichHen() != null && 
                             ba.getLichHen().getNguoiDung() != null && 
                             ba.getLichHen().getNguoiDung().getId().equals(benhNhan.getId()))
                .toList();
    }
    
    @Transactional(readOnly = true)
    public Optional<BenhAn> findByLichHenId(Long lichHenId) {
        // Sửa: tìm bằng cách filter
        return benhAnRepository.findAll().stream()
                .filter(ba -> ba.getLichHen() != null && ba.getLichHen().getId().equals(lichHenId))
                .findFirst();
    }
    
    public BenhAn save(BenhAn benhAn) {
        if (benhAn.getThoiGianTao() == null) {
            benhAn.setThoiGianTao(LocalDateTime.now());
        }
        benhAn.setThoiGianCapNhat(LocalDateTime.now());
        return benhAnRepository.save(benhAn);
    }
    
    public void deleteById(Long id) {
        benhAnRepository.deleteById(id);
    }
    
    // Tạo bệnh án từ lịch hẹn
    public BenhAn taoBenhAnTuLichHen(Long lichHenId) {
        Optional<LichHen> lichHenOpt = lichHenRepository.findById(lichHenId);
        if (lichHenOpt.isPresent()) {
            LichHen lichHen = lichHenOpt.get();
            
            // Kiểm tra xem đã có bệnh án cho lịch hẹn này chưa
            Optional<BenhAn> existingBenhAn = findByLichHenId(lichHenId);
            if (existingBenhAn.isPresent()) {
                return existingBenhAn.get();
            }
            
            BenhAn benhAn = new BenhAn();
            benhAn.setLichHen(lichHen);
            benhAn.setBacSi(lichHen.getBacSi());
            benhAn.setThoiGianTao(LocalDateTime.now());
            
            return benhAnRepository.save(benhAn);
        }
        return null;
    }
    
    // Cập nhật chẩn đoán
    public boolean capNhatChanDoan(Long id, String chanDoan) {
        Optional<BenhAn> benhAnOpt = benhAnRepository.findById(id);
        if (benhAnOpt.isPresent()) {
            BenhAn benhAn = benhAnOpt.get();
            benhAn.setChanDoan(chanDoan);
            benhAn.setThoiGianCapNhat(LocalDateTime.now());
            benhAnRepository.save(benhAn);
            return true;
        }
        return false;
    }
    
    // Cập nhật đơn thuốc
    public boolean capNhatDonThuoc(Long id, String donThuoc) {
        Optional<BenhAn> benhAnOpt = benhAnRepository.findById(id);
        if (benhAnOpt.isPresent()) {
            BenhAn benhAn = benhAnOpt.get();
            benhAn.setDonThuoc(donThuoc);
            benhAn.setThoiGianCapNhat(LocalDateTime.now());
            benhAnRepository.save(benhAn);
            return true;
        }
        return false;
    }
    
    // Cập nhật phác đồ điều trị
    public boolean capNhatPhacDoDieuTri(Long id, String phacDoDieuTri) {
        Optional<BenhAn> benhAnOpt = benhAnRepository.findById(id);
        if (benhAnOpt.isPresent()) {
            BenhAn benhAn = benhAnOpt.get();
            benhAn.setPhacDoDieuTri(phacDoDieuTri);
            benhAn.setThoiGianCapNhat(LocalDateTime.now());
            benhAnRepository.save(benhAn);
            return true;
        }
        return false;
    }
    
    @Transactional(readOnly = true)
    public long demBenhAnTheoBacSi(NguoiDung bacSi) {
        return benhAnRepository.findAll().stream()
                .filter(ba -> ba.getBacSi() != null && ba.getBacSi().getId().equals(bacSi.getId()))
                .count();
    }
}