import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration

public class Router {
    @RequestMapping(value="/rest-v1/ping", method=RequestMethod.GET)
    String ping() {
        return "pong\n";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Router.class, args);
    }
}
