package hello;

public class ConsoleHelloService implements HelloService{

	public String sayHello() {

		System.out.println("Hello from console!");
		return "Hello";
	}

}
