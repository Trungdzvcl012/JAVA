package com.nhakhoa.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "lich_lam_viec")
public class LichLamViec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bác sĩ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bac_si_id", nullable = false)
    private NguoiDung bacSi;

    @NotNull(message = "Ngày làm việc không được để trống")
    @Column(name = "ngay_lam_viec", nullable = false)
    private LocalDate ngayLamViec;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    @Column(name = "gio_bat_dau", nullable = false)
    private LocalTime gioBatDau;

    @NotNull(message = "Giờ kết thúc không được để trống")
    @Column(name = "gio_ket_thuc", nullable = false)
    private LocalTime gioKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiLichLamViec trangThai;

    @Column(name = "ghi_chu")
    private String ghiChu;

    // Constructors
    public LichLamViec() {
        this.trangThai = TrangThaiLichLamViec.TRONG;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public NguoiDung getBacSi() { return bacSi; }
    public void setBacSi(NguoiDung bacSi) { this.bacSi = bacSi; }
    public LocalDate getNgayLamViec() { return ngayLamViec; }
    public void setNgayLamViec(LocalDate ngayLamViec) { this.ngayLamViec = ngayLamViec; }
    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
    public TrangThaiLichLamViec getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiLichLamViec trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}