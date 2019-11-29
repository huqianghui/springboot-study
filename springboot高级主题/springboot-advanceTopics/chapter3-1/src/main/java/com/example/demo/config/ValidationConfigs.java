package com.example.demo.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * *** WARNING ***
 * In this example the annotations to validate the imported properties come from Hibernate-Validator package.
 * ***************
 *
 * One of the handy things that this annotation provides the is the validation of properties using the JSR-303 format.
 * This allows for all sorts of neat things.
 *
 * This helps us reduce a lot of if – else conditions in our code and makes it look much cleaner and concise.
 *
 * If any of these validations fail then the main application would fail to start with an IllegalStateException till the incorrect property is corrected.
 *
 * PLEASE NOTE
 *	. Also, it is important that we declare getters and setters for each of the properties as they’re used by the validator framework to access the
 *	  concerned properties.
 *	. {@link org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor} advice to user @Validated annotation on this
 *	  kind of classes
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Configuration
@ConfigurationProperties(prefix = "validation")
@PropertySource("classpath:/other-configs/validation.properties")
@Validated
public class ValidationConfigs {

	/**
	 * @NotBlank: The string is not null and the trimmed length is greater than zero. [deprecated]
	 * @NotEmpty: The CharSequence, Collection, Map or Array object is not null and size > 0. [deprecated]
	 * @NotNull:  The CharSequence, Collection, Map or Array object is not null, but can be empty.
	 */
	//@NotBlank
	//@NotEmpty
	@NotNull
	private String notBlank;

	@Length(max = 4, min = 1)
	private String lengthOfString;
	
	@Min(1025)
	@Max(65536)
	private int numerical;
	
	@Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$")
	private String emailPattern;
	
}
