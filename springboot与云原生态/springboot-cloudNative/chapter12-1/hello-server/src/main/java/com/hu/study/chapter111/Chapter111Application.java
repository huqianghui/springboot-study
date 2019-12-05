package com.hu.study.chapter111;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Chapter111Application {

	public static void main(String[] args) {
		SpringApplication.run(Chapter111Application.class, args);
	}

	@GetMapping("/hello")
	public String hello(){
		return "hello world.";
	}
}
