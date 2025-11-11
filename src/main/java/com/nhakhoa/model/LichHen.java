package com.nhakhoa.model;

import java.time.LocalDateTime;

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
@Table(name = "lich_hen")
public class LichHen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bệnh nhân không được để trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "benh_nhan_id", nullable = false)
    private NguoiDung nguoiDung; // Field name phải khớp với "mappedBy = 'nguoiDung'"

    @NotNull(message = "Bác sĩ không được để trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bac_si_id", nullable = false)
    private NguoiDung bacSi;

    @NotNull(message = "Dịch vụ không được để trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dich_vu_id", nullable = false)
    private DichVu dichVu;

    @NotNull(message = "Thời gian hẹn không được để trống")
    @Column(name = "thoi_gian_hen", nullable = false)
    private LocalDateTime thoiGianHen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrangThaiLichHen trangThai;

    @Column(name = "ly_do_kham")
    private String lyDoKham;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "thoi_gian_tao")
    private LocalDateTime thoiGianTao;

    @Column(name = "thoi_gian_cap_nhat")
    private LocalDateTime thoiGianCapNhat;

    // Constructors
    public LichHen() {
        this.thoiGianTao = LocalDateTime.now();
        this.thoiGianCapNhat = LocalDateTime.now();
        this.trangThai = TrangThaiLichHen.CHO_XAC_NHAN;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public NguoiDung getNguoiDung() { return nguoiDung; }
    public void setNguoiDung(NguoiDung nguoiDung) { this.nguoiDung = nguoiDung; }
    
    public NguoiDung getBacSi() { return bacSi; }
    public void setBacSi(NguoiDung bacSi) { this.bacSi = bacSi; }
    
    public DichVu getDichVu() { return dichVu; }
    public void setDichVu(DichVu dichVu) { this.dichVu = dichVu; }
    
    public LocalDateTime getThoiGianHen() { return thoiGianHen; }
    public void setThoiGianHen(LocalDateTime thoiGianHen) { this.thoiGianHen = thoiGianHen; }
    
    public TrangThaiLichHen getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiLichHen trangThai) { this.trangThai = trangThai; }
    
    public String getLyDoKham() { return lyDoKham; }
    public void setLyDoKham(String lyDoKham) { this.lyDoKham = lyDoKham; }
    
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    
    public LocalDateTime getThoiGianTao() { return thoiGianTao; }
    public void setThoiGianTao(LocalDateTime thoiGianTao) { this.thoiGianTao = thoiGianTao; }
    
    public LocalDateTime getThoiGianCapNhat() { return thoiGianCapNhat; }
    public void setThoiGianCapNhat(LocalDateTime thoiGianCapNhat) { this.thoiGianCapNhat = thoiGianCapNhat; }

    // Helper method để hiển thị trạng thái
    public String getTrangThaiDisplayName() {
        switch (this.trangThai) {
            case CHO_XAC_NHAN: return "Chờ Xác Nhận";
            case DA_XAC_NHAN: return "Đã Xác Nhận";
            case DA_HUY: return "Đã Hủy";
            case DA_HOAN_THANH: return "Đã Hoàn Thành";
            case KHONG_DEN: return "Không Đến";
            default: return "Không xác định";
        }
    }
}