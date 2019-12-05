package com.hu.study.chapter71;

import com.hu.study.chapter71.domain.User;
import com.hu.study.chapter71.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@EnableCaching
@RestController
@EnableJpaRepositories(basePackages = "com.hu.study.chapter71.domain")
public class Application {

	@Autowired
	UserRepository  userRepository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@GetMapping("/user")
	public List<User> getUserByName(@RequestParam("name") String name){
		return userRepository.findByName(name);
	}

}
