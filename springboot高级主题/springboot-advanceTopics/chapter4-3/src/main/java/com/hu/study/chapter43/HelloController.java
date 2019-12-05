package com.hu.study.chapter43;

import com.hu.study.chapter43.resobject.Greeting;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping("/hello")
    public Greeting index() {
        Greeting greeting = new Greeting();
        greeting.setId(123456789L);
        greeting.setContent("hello.");
        return greeting;
    }

}