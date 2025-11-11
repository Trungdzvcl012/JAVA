package com.nhakhoa.repository;

import com.nhakhoa.model.LichLamViec;
import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.TrangThaiLichLamViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface LichLamViecRepository extends JpaRepository<LichLamViec, Long> {
    List<LichLamViec> findByBacSiAndNgayLamViec(NguoiDung bacSi, LocalDate ngayLamViec);
    List<LichLamViec> findByBacSiAndNgayLamViecBetween(NguoiDung bacSi, LocalDate startDate, LocalDate endDate);
    List<LichLamViec> findByNgayLamViecAndTrangThai(LocalDate ngayLamViec, TrangThaiLichLamViec trangThai);
    
    @Query("SELECT llv FROM LichLamViec llv WHERE llv.bacSi = :bacSi AND llv.ngayLamViec = :ngay " +
           "AND llv.trangThai = 'TRONG' AND llv.gioBatDau >= :gioBatDau")
    List<LichLamViec> findAvailableSlots(@Param("bacSi") NguoiDung bacSi, 
                                        @Param("ngay") LocalDate ngay, 
                                        @Param("gioBatDau") LocalTime gioBatDau);
}