package com.hu.study.chapter13;

import org.springframework.stereotype.Component;

@Component
public class TestService {

    public String hello(){
        System.out.println("hello");
        return "hello";
    }
}
