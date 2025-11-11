package com.nhakhoa.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nhakhoa.model.LichHen;
import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.TrangThaiLichHen;

@Repository
public interface LichHenRepository extends JpaRepository<LichHen, Long> {
    
    // Sử dụng JOIN FETCH để load tất cả quan hệ
    @Query("SELECT lh FROM LichHen lh " +
           "JOIN FETCH lh.nguoiDung " +
           "JOIN FETCH lh.bacSi " +
           "JOIN FETCH lh.dichVu " +
           "WHERE lh.nguoiDung = :nguoiDung " +
           "ORDER BY lh.thoiGianHen DESC")
    List<LichHen> findByNguoiDungOrderByThoiGianHenDesc(@Param("nguoiDung") NguoiDung nguoiDung);
    
    @Query("SELECT lh FROM LichHen lh " +
           "JOIN FETCH lh.nguoiDung " +
           "JOIN FETCH lh.bacSi " +
           "JOIN FETCH lh.dichVu " +
           "WHERE lh.bacSi = :bacSi " +
           "ORDER BY lh.thoiGianHen DESC")
    List<LichHen> findByBacSiOrderByThoiGianHenDesc(@Param("bacSi") NguoiDung bacSi);
    
    // GIỮ LẠI method với @Query (có JOIN FETCH) - XÓA method không có @Query ở cuối file
    @Query("SELECT lh FROM LichHen lh " +
           "JOIN FETCH lh.nguoiDung " +
           "JOIN FETCH lh.bacSi " +
           "JOIN FETCH lh.dichVu " +
           "WHERE lh.trangThai = :trangThai")
    List<LichHen> findByTrangThai(@Param("trangThai") TrangThaiLichHen trangThai);
    
    @Query("SELECT lh FROM LichHen lh " +
           "JOIN FETCH lh.nguoiDung " +
           "JOIN FETCH lh.bacSi " +
           "JOIN FETCH lh.dichVu " +
           "WHERE lh.thoiGianHen BETWEEN :start AND :end")
    List<LichHen> findByThoiGianHenBetween(@Param("start") LocalDateTime start, 
                                          @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(lh) FROM LichHen lh WHERE DATE(lh.thoiGianHen) = :ngay AND lh.trangThai = 'DA_HOAN_THANH'")
    long countCompletedAppointmentsByDate(@Param("ngay") LocalDate ngay);
    
    @Query("SELECT lh FROM LichHen lh " +
           "JOIN FETCH lh.nguoiDung " +
           "JOIN FETCH lh.bacSi " +
           "JOIN FETCH lh.dichVu " +
           "WHERE lh.thoiGianHen BETWEEN :start AND :end " +
           "AND lh.trangThai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN')")
    List<LichHen> findUpcomingAppointments(@Param("start") LocalDateTime start, 
                                          @Param("end") LocalDateTime end);
    
    // Thêm method findAll với JOIN FETCH
    @Query("SELECT lh FROM LichHen lh " +
           "JOIN FETCH lh.nguoiDung " +
           "JOIN FETCH lh.bacSi " +
           "JOIN FETCH lh.dichVu " +
           "ORDER BY lh.thoiGianHen DESC")
    List<LichHen> findAllWithDetails();
    
    // CHỈ GIỮ LẠI các method không trùng lặp
    List<LichHen> findByNguoiDung(NguoiDung nguoiDung);
    List<LichHen> findByBacSi(NguoiDung bacSi);
    // XÓA dòng này: List<LichHen> findByTrangThai(TrangThaiLichHen trangThai);
}