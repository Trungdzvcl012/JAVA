package com.nhakhoa.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.service.NguoiDungService;

@Controller
@RequestMapping("/nguoi-dung")
public class NguoiDungController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping("/ho-so")
    public String xemHoSo(Model model, Principal principal) {
        String email = principal.getName();
        NguoiDung nguoiDung = nguoiDungService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        model.addAttribute("nguoiDung", nguoiDung);
        return "nguoi-dung/ho-so";
    }

    @PostMapping("/ho-so/cap-nhat")
    public String capNhatHoSo(@ModelAttribute NguoiDung formNguoiDung,
                              @RequestParam("anhDaiDienFile") MultipartFile file,
                              Principal principal) throws IOException {

        NguoiDung nguoiDung = nguoiDungService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Cập nhật thông tin
        nguoiDung.setHoTen(formNguoiDung.getHoTen());
        nguoiDung.setSoDienThoai(formNguoiDung.getSoDienThoai());
        nguoiDung.setDiaChi(formNguoiDung.getDiaChi());

        // Lưu file ảnh nếu có
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String uploadDir = "uploads/avatars/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            nguoiDung.setAnhDaiDien("/uploads/avatars/" + fileName);
        }

        nguoiDungService.save(nguoiDung);
        return "redirect:/nguoi-dung/ho-so";
    }
}

