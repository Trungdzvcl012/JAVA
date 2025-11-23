package com.nhakhoa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhakhoa.model.HoaDon;
import com.nhakhoa.repository.HoaDonRepository;

@Service
public class HoaDonService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    // Tìm hóa đơn theo id
    public HoaDon findById(Long id) {
        Optional<HoaDon> hoaDonOpt = hoaDonRepository.findById(id);
        return hoaDonOpt.orElse(null);
    }

    // Lưu hoặc cập nhật hóa đơn
    public HoaDon save(HoaDon hoaDon) {
        return hoaDonRepository.save(hoaDon);
    }

    // Lấy tất cả hóa đơn theo trạng thái
    public List<HoaDon> findByTrangThai(com.nhakhoa.model.TrangThaiHoaDon trangThai) {
        return hoaDonRepository.findByTrangThai(trangThai);
    }
}
