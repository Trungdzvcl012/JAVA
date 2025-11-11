package com.nhakhoa.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "benh_an")
public class BenhAn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lich_hen_id", nullable = false)
    private LichHen lichHen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bac_si_id", nullable = false)
    private NguoiDung bacSi;

    @Column(columnDefinition = "TEXT")
    private String chanDoan;

    @Column(name = "phac_do_dieu_tri", columnDefinition = "TEXT")
    private String phacDoDieuTri;

    @Column(name = "don_thuoc", columnDefinition = "TEXT")
    private String donThuoc;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "hinh_anh_x_quang")
    private String hinhAnhXQuang;

    @Column(name = "hinh_anh_khac")
    private String hinhAnhKhac;

    @Column(name = "thoi_gian_tao")
    private LocalDateTime thoiGianTao;

    @Column(name = "thoi_gian_cap_nhat")
    private LocalDateTime thoiGianCapNhat;

    // Constructors
    public BenhAn() {
        this.thoiGianTao = LocalDateTime.now();
        this.thoiGianCapNhat = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LichHen getLichHen() { return lichHen; }
    public void setLichHen(LichHen lichHen) { this.lichHen = lichHen; }

    public NguoiDung getBacSi() { return bacSi; }
    public void setBacSi(NguoiDung bacSi) { this.bacSi = bacSi; }

    public String getChanDoan() { return chanDoan; }
    public void setChanDoan(String chanDoan) { this.chanDoan = chanDoan; }

    public String getPhacDoDieuTri() { return phacDoDieuTri; }
    public void setPhacDoDieuTri(String phacDoDieuTri) { this.phacDoDieuTri = phacDoDieuTri; }

    public String getDonThuoc() { return donThuoc; }
    public void setDonThuoc(String donThuoc) { this.donThuoc = donThuoc; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getHinhAnhXQuang() { return hinhAnhXQuang; }
    public void setHinhAnhXQuang(String hinhAnhXQuang) { this.hinhAnhXQuang = hinhAnhXQuang; }

    public String getHinhAnhKhac() { return hinhAnhKhac; }
    public void setHinhAnhKhac(String hinhAnhKhac) { this.hinhAnhKhac = hinhAnhKhac; }

    public LocalDateTime getThoiGianTao() { return thoiGianTao; }
    public void setThoiGianTao(LocalDateTime thoiGianTao) { this.thoiGianTao = thoiGianTao; }

    public LocalDateTime getThoiGianCapNhat() { return thoiGianCapNhat; }
    public void setThoiGianCapNhat(LocalDateTime thoiGianCapNhat) { this.thoiGianCapNhat = thoiGianCapNhat; }
}