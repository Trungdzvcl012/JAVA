package com.nhakhoa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nhakhoa.model.DichVu;

@Repository
public interface DichVuRepository extends JpaRepository<DichVu, Long> {
    List<DichVu> findByDangKhuyenMaiTrue();
    
    @Query("SELECT dv FROM DichVu dv ORDER BY dv.tenDichVu ASC")
    List<DichVu> findAllOrderByTen();
}