package com.nhakhoa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChinhSachController {


    @GetMapping("/chinh-sach-bao-mat")
    public String trangBaoMat() {
        return "chinh-sach-bao-mat";
    }

 
    @GetMapping("/dieu-khoan-su-dung")
    public String trangDieuKhoan() {
        return "dieu-khoan-su-dung";
    }
}
