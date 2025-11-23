package com.nhakhoa.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QuizController {

    @GetMapping("/kiem-tra-suc-khoe")
    public String showQuizPage() {
        return "quiz"; 
    }
}

