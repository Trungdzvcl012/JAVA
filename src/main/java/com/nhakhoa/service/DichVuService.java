package com.nhakhoa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhakhoa.model.DichVu;
import com.nhakhoa.repository.DichVuRepository;

@Service
public class DichVuService {
    
    @Autowired
    private DichVuRepository dichVuRepository;
    
    public List<DichVu> findAll() {
        return dichVuRepository.findAllOrderByTen();
    }
    
    public Optional<DichVu> findById(Long id) {
        return dichVuRepository.findById(id);
    }
    
    public DichVu save(DichVu dichVu) {
        return dichVuRepository.save(dichVu);
    }
    
    public void deleteById(Long id) {
        dichVuRepository.deleteById(id);
    }
    
    public List<DichVu> findDichVuKhuyenMai() {
        return dichVuRepository.findByDangKhuyenMaiTrue();
    }
}