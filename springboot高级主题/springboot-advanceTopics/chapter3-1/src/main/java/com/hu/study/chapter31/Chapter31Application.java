package com.hu.study.chapter31;

import com.example.demo.config.AppProperties;
import com.example.demo.config.EmailConfigs;
import com.example.demo.config.ValidationConfigs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class,EmailConfigs.class,ValidationConfigs.class})
public class Chapter31Application {

	public static void main(String[] args) {
		SpringApplication.run(Chapter31Application.class, args);
	}

}
