package com.hu.study.chapter13;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    TestService testService;

    @RequestMapping("/hello")
    public String sayHello() {
        testService.hello();
        return "Hello World! ";
    }

}