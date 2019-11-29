package com.hu.study.chapter91;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.hu.study.chapter91.repository")
@SpringBootApplication
public class SpringLogbackExampleApplication {


	public static void main(String[] args) {
		SpringApplication.run(SpringLogbackExampleApplication.class, args);
	}
}
