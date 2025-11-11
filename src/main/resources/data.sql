-- Thêm dữ liệu mẫu cho bảng nguoi_dung với mật khẩu đơn giản: "123456"
INSERT INTO nguoi_dung (ho_ten, email, mat_khau, so_dien_thoai, ngay_sinh, dia_chi, anh_dai_dien, vai_tro, da_kich_hoat, thoi_gian_tao) VALUES
('Admin System', 'admin@nhakhoa.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV9UiE', '0901234567', '1980-01-01', 'Ha Noi', 'admin.jpg', 'ADMIN', true, CURRENT_TIMESTAMP),
('Nguyen Van A', 'patient1@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV9UiE', '0912345678', '1990-05-15', 'Ha Noi', 'patient1.jpg', 'PATIENT', true, CURRENT_TIMESTAMP),
('Tran Thi B', 'patient2@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV9UiE', '0923456789', '1985-08-20', 'Ho Chi Minh', 'patient2.jpg', 'PATIENT', true, CURRENT_TIMESTAMP),
('BS. Le Van C', 'bacsile@nhakhoa.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV9UiE', '0934567890', '1975-03-10', 'Ha Noi', 'bacsile.jpg', 'DOCTOR', true, CURRENT_TIMESTAMP),
('BS. Pham Thi D', 'bacsipham@nhakhoa.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV9UiE', '0945678901', '1982-11-25', 'Da Nang', 'bacsipham.jpg', 'DOCTOR', true, CURRENT_TIMESTAMP),
('Nhan vien E', 'nhanvien@nhakhoa.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV9UiE', '0956789012', '1995-07-30', 'Ha Noi', 'nhanvien.jpg', 'STAFF', true, CURRENT_TIMESTAMP);

-- Thêm dữ liệu mẫu cho bảng dich_vu
INSERT INTO dich_vu (ten_dich_vu, mo_ta, gia_dich_vu, thoi_gian_du_kien, hinh_anh, dang_khuyen_mai, gia_khuyen_mai) VALUES
('Kham tong quat', 'Kham va tu van tong quat ve suc khoe rang mieng', 200000, 30, 'kham-tong-quat.jpg', false, NULL),
('Lay cao rang', 'Lay cao rang va danh bong be mat rang', 300000, 45, 'lay-cao-rang.jpg', true, 250000),
('Tram rang', 'Tram rang sau bang composite', 400000, 60, 'tram-rang.jpg', false, NULL),
('Nho rang khon', 'Nho rang khon khong bien chung', 1500000, 90, 'nho-rang-khon.jpg', false, NULL),
('Tay trang rang', 'Tay trang rang cong nghe Laser', 2500000, 120, 'tay-trang-rang.jpg', true, 2000000),
('Nieng rang', 'Tu van va len phac do nieng rang', 500000, 60, 'nieng-rang.jpg', false, NULL);

-- Thêm dữ liệu mẫu cho bảng lich_lam_viec
INSERT INTO lich_lam_viec (bac_si_id, ngay_lam_viec, gio_bat_dau, gio_ket_thuc, trang_thai, ghi_chu) VALUES
(4, CURRENT_DATE + 1, '08:00:00', '12:00:00', 'TRONG', 'Lam viec buoi sang'),
(4, CURRENT_DATE + 1, '13:00:00', '17:00:00', 'TRONG', 'Lam viec buoi chieu'),
(5, CURRENT_DATE + 1, '08:00:00', '12:00:00', 'TRONG', 'Lam viec buoi sang'),
(5, CURRENT_DATE + 1, '13:00:00', '17:00:00', 'TRONG', 'Lam viec buoi chieu'),
(4, CURRENT_DATE + 2, '08:00:00', '12:00:00', 'TRONG', 'Lam viec buoi sang'),
(5, CURRENT_DATE + 2, '13:00:00', '17:00:00', 'TRONG', 'Lam viec buoi chieu');

-- Thêm dữ liệu mẫu cho bảng lich_hen
INSERT INTO lich_hen (benh_nhan_id, bac_si_id, dich_vu_id, thoi_gian_hen, trang_thai, ly_do_kham, ghi_chu, thoi_gian_tao, thoi_gian_cap_nhat) VALUES
(2, 4, 2, TIMESTAMPADD('DAY', 2, CURRENT_TIMESTAMP), 'DA_XAC_NHAN', 'Rang bi o vang', 'Can lay cao rang dinh ky', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 5, 3, TIMESTAMPADD('DAY', 3, CURRENT_TIMESTAMP), 'CHO_XAC_NHAN', 'Rang sau ham duoi', 'Dau nhuc nhe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 4, 1, TIMESTAMPADD('DAY', -5, CURRENT_TIMESTAMP), 'DA_HOAN_THANH', 'Kham dinh ky', 'Suc khoe rang mieng tot', TIMESTAMPADD('DAY', -10, CURRENT_TIMESTAMP), TIMESTAMPADD('DAY', -5, CURRENT_TIMESTAMP));

-- Thêm dữ liệu mẫu cho bảng benh_an
INSERT INTO benh_an (lich_hen_id, bac_si_id, chan_doan, phac_do_dieu_tri, don_thuoc, ghi_chu, thoi_gian_tao, thoi_gian_cap_nhat) VALUES
(3, 4, 'Suc khoe rang mieng tot, khong co van de nghiem trong', 'Ve sinh rang mieng dung cach, kham dinh ky 6 thang/lan', 'Khong can thuoc', 'Benh nhan can duy tri thoi quen ve sinh rang mieng tot', TIMESTAMPADD('DAY', -5, CURRENT_TIMESTAMP), TIMESTAMPADD('DAY', -5, CURRENT_TIMESTAMP));

-- Thêm dữ liệu mẫu cho bảng hoa_don
INSERT INTO hoa_don (lich_hen_id, tong_tien, da_thanh_toan, trang_thai, phuong_thuc_thanh_toan, thoi_gian_tao, thoi_gian_thanh_toan) VALUES
(3, 200000, 200000, 'DA_THANH_TOAN', 'TIEN_MAT', TIMESTAMPADD('DAY', -5, CURRENT_TIMESTAMP), TIMESTAMPADD('DAY', -5, CURRENT_TIMESTAMP));
