package com.finalproject_sns.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {

    @GetMapping()
    public String hello() {
        return "이아진";
    }

    @GetMapping("/{num}")
    public String sumOfDigit(@PathVariable String num) {
        int sum = 0;
        for (int i = 0; i < num.length(); i++) {
            sum += num.charAt(i) - '0';
        }
        return String.valueOf(sum);
    }


}
