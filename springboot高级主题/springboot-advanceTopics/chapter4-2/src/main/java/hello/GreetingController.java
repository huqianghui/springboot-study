package hello;

import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @CrossOrigin(origins =
//            "*"
            {"http://localhost:9000", "https://localhost:9000", "http://localhost:8001", "https://localhost:8001"}
//            ,
//            methods = RequestMethod.GET, allowedHeaders = "Authorization"
    )
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(required=false, defaultValue="World") String name) {
        System.out.println("==== in greeting ====");
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/greeting-javaconfig")
    public Greeting greetingWithJavaconfig(@RequestParam(required=false, defaultValue="World") String name) {
        System.out.println("==== in greeting-javaconfig ====");
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

}
