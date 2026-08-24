package io.github.devup.tripfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //@createDate @LastModifiedDate 이걸사용하려고 사용
@SpringBootApplication
public class TripFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(TripFinderApplication.class, args);
    }

}
