package com.finalproject_sns.service;


import com.finalproject_sns.controller.HelloController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloServiceTest {

    // spring 없이 테스트 하기 때문에 new를 이용해 초기화 해주기
    HelloService helloService = new HelloService();

    @Test
    @DisplayName("합 테스트")
    void sum_of_digit_success() {
        assertEquals("3",helloService.sumOfDigit("111"));
        assertEquals("6",helloService.sumOfDigit("222"));
        assertEquals("9",helloService.sumOfDigit("333"));
    }

}