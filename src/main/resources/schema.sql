-- Tạo bảng nguoi_dung
CREATE TABLE IF NOT EXISTS nguoi_dung (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ho_ten VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mat_khau VARCHAR(255) NOT NULL,
    so_dien_thoai VARCHAR(15),
    ngay_sinh VARCHAR(20),
    dia_chi TEXT,
    anh_dai_dien VARCHAR(255),
    vai_tro VARCHAR(20) NOT NULL,
    da_kich_hoat BOOLEAN DEFAULT TRUE,
    thoi_gian_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng dich_vu
CREATE TABLE IF NOT EXISTS dich_vu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ten_dich_vu VARCHAR(255) NOT NULL,
    mo_ta TEXT,
    gia_dich_vu DECIMAL(15,2),
    thoi_gian_du_kien INT,
    hinh_anh VARCHAR(255),
    dang_khuyen_mai BOOLEAN DEFAULT FALSE,
    gia_khuyen_mai DECIMAL(15,2)
);

-- Tạo bảng lich_lam_viec
CREATE TABLE IF NOT EXISTS lich_lam_viec (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bac_si_id BIGINT,
    ngay_lam_viec DATE,
    gio_bat_dau TIME,
    gio_ket_thuc TIME,
    trang_thai VARCHAR(20),
    ghi_chu TEXT
);

-- Tạo bảng lich_hen
CREATE TABLE IF NOT EXISTS lich_hen (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    benh_nhan_id BIGINT,
    bac_si_id BIGINT,
    dich_vu_id BIGINT,
    thoi_gian_hen TIMESTAMP,
    trang_thai VARCHAR(20),
    ly_do_kham TEXT,
    ghi_chu TEXT,
    thoi_gian_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    thoi_gian_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng benh_an
CREATE TABLE IF NOT EXISTS benh_an (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lich_hen_id BIGINT,
    bac_si_id BIGINT,
    chan_doan TEXT,
    phac_do_dieu_tri TEXT,
    don_thuoc TEXT,
    ghi_chu TEXT,
    thoi_gian_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    thoi_gian_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng hoa_don
CREATE TABLE IF NOT EXISTS hoa_don (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lich_hen_id BIGINT,
    tong_tien DECIMAL(15,2),
    da_thanh_toan DECIMAL(15,2),
    trang_thai VARCHAR(20),
    phuong_thuc_thanh_toan VARCHAR(50),
    thoi_gian_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    thoi_gian_thanh_toan TIMESTAMP
);