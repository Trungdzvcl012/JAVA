package com.nhakhoa.repository;

import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    Optional<NguoiDung> findByEmail(String email);
    List<NguoiDung> findByVaiTro(VaiTro vaiTro);
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM NguoiDung u WHERE u.vaiTro = 'DOCTOR' AND u.daKichHoat = true")
    List<NguoiDung> findAllBacSiDangHoatDong();
    
    @Query("SELECT COUNT(u) FROM NguoiDung u WHERE u.vaiTro = :vaiTro")
    long countByVaiTro(@Param("vaiTro") VaiTro vaiTro);
}