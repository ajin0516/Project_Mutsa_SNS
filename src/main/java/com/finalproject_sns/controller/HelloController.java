package com.finalproject_sns.controller;

import com.finalproject_sns.service.HelloService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
@RequiredArgsConstructor
@Api(tags = "CD/CD TEST")
public class HelloController {

    private final HelloService helloService;

    @GetMapping()
    public String hello() {
        return "이아진";
    }

    @GetMapping("/{num}")
    public String sumOfDigit(@PathVariable String num) {
        return helloService.sumOfDigit(num);
    }


}
