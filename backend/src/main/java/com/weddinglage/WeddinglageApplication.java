package com.weddinglage;

import com.weddinglage.config.WeddinglageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(WeddinglageProperties.class)
public class WeddinglageApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeddinglageApplication.class, args);
    }
}
