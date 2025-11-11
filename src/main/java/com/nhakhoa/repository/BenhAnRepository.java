package com.nhakhoa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nhakhoa.model.BenhAn;
import com.nhakhoa.model.NguoiDung;

@Repository
public interface BenhAnRepository extends JpaRepository<BenhAn, Long> {
    List<BenhAn> findByBacSi(NguoiDung bacSi);
    
    @Query("SELECT ba FROM BenhAn ba WHERE ba.lichHen.nguoiDung = :benhNhan ORDER BY ba.thoiGianTao DESC")
    List<BenhAn> findByBenhNhan(@Param("benhNhan") NguoiDung benhNhan);
}