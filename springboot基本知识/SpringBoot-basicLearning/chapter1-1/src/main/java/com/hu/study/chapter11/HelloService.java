package com.hu.study.chapter11;

import org.springframework.stereotype.Component;

@Component
public class HelloService {

    public String sayHello() {
        return "Hello World";
    }

}