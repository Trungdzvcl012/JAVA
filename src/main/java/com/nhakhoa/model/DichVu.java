package com.nhakhoa.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dich_vu")
public class DichVu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên dịch vụ không được để trống")
    @Column(name = "ten_dich_vu", nullable = false, unique = true)
    private String tenDichVu;

    @Column(columnDefinition = "TEXT")
    private String moTa;

    @NotNull(message = "Giá dịch vụ không được để trống")
    @Column(name = "gia_dich_vu", nullable = false)
    private BigDecimal giaDichVu;

    @Column(name = "thoi_gian_du_kien")
    private Integer thoiGianDuKien; // in minutes

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "dang_khuyen_mai")
    private boolean dangKhuyenMai = false;

    @Column(name = "gia_khuyen_mai")
    private BigDecimal giaKhuyenMai;

    @OneToMany(mappedBy = "dichVu", cascade = CascadeType.ALL)
    private List<LichHen> lichHens = new ArrayList<>();

    // Constructors
    public DichVu() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public BigDecimal getGiaDichVu() { return giaDichVu; }
    public void setGiaDichVu(BigDecimal giaDichVu) { this.giaDichVu = giaDichVu; }

    public Integer getThoiGianDuKien() { return thoiGianDuKien; }
    public void setThoiGianDuKien(Integer thoiGianDuKien) { this.thoiGianDuKien = thoiGianDuKien; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public boolean isDangKhuyenMai() { return dangKhuyenMai; }
    public void setDangKhuyenMai(boolean dangKhuyenMai) { this.dangKhuyenMai = dangKhuyenMai; }

    public BigDecimal getGiaKhuyenMai() { return giaKhuyenMai; }
    public void setGiaKhuyenMai(BigDecimal giaKhuyenMai) { this.giaKhuyenMai = giaKhuyenMai; }

    public List<LichHen> getLichHens() { return lichHens; }
    public void setLichHens(List<LichHen> lichHens) { this.lichHens = lichHens; }
}