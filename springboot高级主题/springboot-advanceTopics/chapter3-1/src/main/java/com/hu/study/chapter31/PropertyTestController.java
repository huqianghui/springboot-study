package com.hu.study.chapter31;

import com.example.demo.config.EmailConfigs;
import com.example.demo.config.ValidationConfigs;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class PropertyTestController {
	
	/**
	 * To obtain the value of a property with the new Environment API (part 1)
	 */
	@Resource(type = Environment.class)
	@Getter
	private Environment springEnvironment;
	
	@Resource(type = EmailConfigs.class)
	@Getter
	private EmailConfigs emailConfigs;

	@Resource(type = ValidationConfigs.class)
	@Getter
	private ValidationConfigs validConfigs;


	@GetMapping("/mail")
	public String testHierarchicalPropertiesFromOtherFile() {
		return getEmailConfigs().toString();
	}

	@GetMapping("/validation")
	public String testValidationProperties() {
		
		return getValidConfigs().toString();
	}
	
}
