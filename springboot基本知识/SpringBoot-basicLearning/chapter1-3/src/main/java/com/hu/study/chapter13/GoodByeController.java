package com.hu.study.chapter13;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoodByeController {

    @Autowired
    private HelloController helloController;

    @Autowired
    private TestService testService;

    @RequestMapping("/goodbye")
    public String goodbye() {
        testService.hello();
        String helloContext = helloController.sayHello();
        return helloContext + " goodbye.";
    }

}