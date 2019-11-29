package com.hu.study.chapter11;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes =Chapter11Application.class)
public class Chapter11ApplicationTests {

    @Autowired
    private HelloService helloService;

    @Test
    public void sayHello(){
        assertThat(helloService.sayHello()).isEqualTo("Hello World");
    }

}
