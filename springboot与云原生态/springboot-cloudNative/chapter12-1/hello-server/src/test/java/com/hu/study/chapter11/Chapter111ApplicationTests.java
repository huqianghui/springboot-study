package com.hu.study.chapter11;

import com.hu.study.chapter111.Chapter111Application;
import com.hu.study.chapter111.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Chapter111Application.class)
public class Chapter111ApplicationTests {

    @Autowired
    private HelloService helloService;

    @Test
    public void sayHello(){
        assertThat(helloService.sayHello()).isEqualTo("Hello World");
    }

}
