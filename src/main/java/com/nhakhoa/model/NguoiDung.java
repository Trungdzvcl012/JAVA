package com.nhakhoa.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "nguoi_dung")
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không quá 100 ký tự")
    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String matKhau;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "ngay_sinh")
    private String ngaySinh;

    private String diaChi;

    @Column(name = "anh_dai_dien")
    private String anhDaiDien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VaiTro vaiTro;

    @Column(name = "da_kich_hoat")
    private boolean daKichHoat = true; // SỬA: Mặc định là true để admin có thể đăng nhập

    @Column(name = "thoi_gian_tao")
    private LocalDateTime thoiGianTao;

    @Column(length = 1000)
    private String ghiChu;

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL)
    private List<LichHen> lichHens = new ArrayList<>();

    @OneToMany(mappedBy = "bacSi", cascade = CascadeType.ALL)
    private List<LichLamViec> lichLamViecs = new ArrayList<>();

    @OneToMany(mappedBy = "bacSi", cascade = CascadeType.ALL)
    private List<BenhAn> benhAns = new ArrayList<>();

    // Constructors
    public NguoiDung() {
        this.thoiGianTao = LocalDateTime.now();
        this.daKichHoat = true; // Đảm bảo mặc định là true
    }

    // Getters and Setters - GIỮ NGUYÊN
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(String ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getAnhDaiDien() { return anhDaiDien; }
    public void setAnhDaiDien(String anhDaiDien) { this.anhDaiDien = anhDaiDien; }
    public VaiTro getVaiTro() { return vaiTro; }
    public void setVaiTro(VaiTro vaiTro) { this.vaiTro = vaiTro; }
    public boolean isDaKichHoat() { return daKichHoat; }
    public void setDaKichHoat(boolean daKichHoat) { this.daKichHoat = daKichHoat; }
    public LocalDateTime getThoiGianTao() { return thoiGianTao; }
    public void setThoiGianTao(LocalDateTime thoiGianTao) { this.thoiGianTao = thoiGianTao; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public List<LichHen> getLichHens() { return lichHens; }
    public void setLichHens(List<LichHen> lichHens) { this.lichHens = lichHens; }
    public List<LichLamViec> getLichLamViecs() { return lichLamViecs; }
    public void setLichLamViecs(List<LichLamViec> lichLamViecs) { this.lichLamViecs = lichLamViecs; }
    public List<BenhAn> getBenhAns() { return benhAns; }
    public void setBenhAns(List<BenhAn> benhAns) { this.benhAns = benhAns; }
}