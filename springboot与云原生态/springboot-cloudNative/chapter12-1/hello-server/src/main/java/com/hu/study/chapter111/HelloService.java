package com.hu.study.chapter111;

import org.springframework.stereotype.Component;

@Component
public class HelloService {

    public String sayHello() {
        return "Hello World";
    }

}