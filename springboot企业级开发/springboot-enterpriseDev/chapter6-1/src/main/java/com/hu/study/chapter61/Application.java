package com.hu.study.chapter61;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.hu.study.chapter61","com.hu.study.chapter61.web"})
public class Application {

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);

	}

}
