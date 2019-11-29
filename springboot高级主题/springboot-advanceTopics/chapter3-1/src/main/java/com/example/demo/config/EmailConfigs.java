package com.example.demo.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Configuration
@ConfigurationProperties(prefix = "email")
/**
 * We can also optionally define a custom source where we’re storing these properties, else the default location is looked up.
 */
@PropertySource("classpath:/other-configs/email.properties")
public class EmailConfigs {
	
	private String host;
	
	private long port;
	
	private String from;

	private List<String> defaultRecipients;

	private Map<String, Boolean> additionalHeaders;
	
}
