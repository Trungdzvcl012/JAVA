package com.nhakhoa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.VaiTro;


@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    
    Optional<NguoiDung> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<NguoiDung> findByVaiTro(VaiTro vaiTro);
    
    long countByVaiTro(VaiTro vaiTro);
    
    
    List<NguoiDung> findByDaKichHoat(boolean daKichHoat);
    
    List<NguoiDung> findByVaiTroAndDaKichHoat(VaiTro vaiTro, boolean daKichHoat);
    
    @Query("SELECT nd FROM NguoiDung nd WHERE nd.vaiTro = 'DOCTOR' AND nd.daKichHoat = true")
    List<NguoiDung> findAllBacSiDangHoatDong();
    
    // THÊM METHOD MỚI CHO ADMIN
    @Query("SELECT COUNT(nd) FROM NguoiDung nd WHERE nd.vaiTro = :vaiTro")
    long countByVaiTroString(@Param("vaiTro") String vaiTro);
    
    @Query("SELECT COUNT(nd) FROM NguoiDung nd WHERE nd.vaiTro = 'PATIENT' AND FUNCTION('MONTH', nd.thoiGianTao) = FUNCTION('MONTH', CURRENT_DATE) AND FUNCTION('YEAR', nd.thoiGianTao) = FUNCTION('YEAR', CURRENT_DATE)")
    long countNewPatientsThisMonth();
}