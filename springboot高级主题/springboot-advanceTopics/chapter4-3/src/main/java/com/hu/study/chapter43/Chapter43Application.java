package com.hu.study.chapter43;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.hu.study.chapter43","com.hu.study.chapter43.http"})
public class Chapter43Application {

	public static void main(String[] args) {
		SpringApplication.run(Chapter43Application.class, args);
	}

}
