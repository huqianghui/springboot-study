package demo;

import hello.HelloService;
import hello.HelloServiceAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class App {

	@Autowired
	HelloService helloService;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args).getBean(HelloService.class).sayHello();
	}

	/**
	 * Replaces the default HelloService provided by {@link HelloServiceAutoConfiguration}.
	 */
	@GetMapping("/hello")
	public String customHelloServices() {
		return helloService.sayHello();
	}
}
