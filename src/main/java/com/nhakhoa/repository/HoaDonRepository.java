package com.nhakhoa.repository;

import com.nhakhoa.model.HoaDon;
import com.nhakhoa.model.TrangThaiHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {
    List<HoaDon> findByTrangThai(TrangThaiHoaDon trangThai);
    
    @Query("SELECT SUM(hd.tongTien) FROM HoaDon hd WHERE hd.trangThai = 'DA_THANH_TOAN' " +
           "AND DATE(hd.thoiGianThanhToan) = :ngay")
    BigDecimal calculateDailyRevenue(@Param("ngay") LocalDate ngay);
    
    @Query("SELECT SUM(hd.tongTien) FROM HoaDon hd WHERE hd.trangThai = 'DA_THANH_TOAN' " +
           "AND YEAR(hd.thoiGianThanhToan) = :nam AND MONTH(hd.thoiGianThanhToan) = :thang")
    BigDecimal calculateMonthlyRevenue(@Param("nam") int nam, @Param("thang") int thang);
}