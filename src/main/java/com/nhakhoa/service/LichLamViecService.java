package com.nhakhoa.service;

import com.nhakhoa.model.LichLamViec;
import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.TrangThaiLichLamViec;
import com.nhakhoa.repository.LichLamViecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LichLamViecService {
    
    @Autowired
    private LichLamViecRepository lichLamViecRepository;
    
    public List<LichLamViec> findAll() {
        return lichLamViecRepository.findAll();
    }
    
    public Optional<LichLamViec> findById(Long id) {
        return lichLamViecRepository.findById(id);
    }
    
    public List<LichLamViec> findByBacSiAndNgayLamViec(NguoiDung bacSi, LocalDate ngayLamViec) {
        return lichLamViecRepository.findByBacSiAndNgayLamViec(bacSi, ngayLamViec);
    }
    
    public List<LichLamViec> findByNgayLamViecAndTrangThai(LocalDate ngayLamViec, TrangThaiLichLamViec trangThai) {
        return lichLamViecRepository.findByNgayLamViecAndTrangThai(ngayLamViec, trangThai);
    }
    
    public LichLamViec save(LichLamViec lichLamViec) {
        return lichLamViecRepository.save(lichLamViec);
    }
    
    public void deleteById(Long id) {
        lichLamViecRepository.deleteById(id);
    }
}