package com.nhakhoa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LienHeController {


    @GetMapping("/lien-he")
    public String trangLienHe() {
        return "lien-he";
    }

    
    @PostMapping("/submit-contact") 
    public String xuLyGuiLienHe(RedirectAttributes redirectAttributes) {
        
   
        redirectAttributes.addFlashAttribute("thongBao", "Gửi yêu cầu thành công!");
        
    
        return "redirect:/"; 
    }
}
