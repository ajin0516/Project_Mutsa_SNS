package com.finalproject_sns.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String sumOfDigit(String num){
        Integer sum = 0;
        for (int i = 0; i < num.length(); i++) {
            sum += num.charAt(i) - '0';
        }
        return String.valueOf(sum);
    }

}
