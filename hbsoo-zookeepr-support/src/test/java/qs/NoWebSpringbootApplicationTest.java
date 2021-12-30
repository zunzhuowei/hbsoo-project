package qs;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Created by zun.wei on 2021/12/30.
 */
@SpringBootApplication
public class NoWebSpringbootApplicationTest implements ApplicationRunner {


    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(NoWebSpringbootApplicationTest.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (true) {
            final String s = LocalDateTime.now().toString();
            System.out.println("now is " + s);
            TimeUnit.SECONDS.sleep(60);
        }
    }


}
