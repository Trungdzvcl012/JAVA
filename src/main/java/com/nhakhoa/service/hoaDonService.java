package com.nhakhoa.service;

import org.springframework.stereotype.Service; // <-- Dòng cần thêm
import com.nhakhoa.model.HoaDon;

@Service // <-- DÒNG NÀY LÀ BẮT BUỘC ĐỂ KHẮC PHỤC LỖI
public class hoaDonService {

    public HoaDon findById(Long id) {
    
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    public void save(HoaDon hoaDon) {
      
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

}