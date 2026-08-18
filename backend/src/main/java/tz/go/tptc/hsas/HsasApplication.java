package tz.go.tptc.hsas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import tz.go.tptc.hsas.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class HsasApplication {
    public static void main(String[] args) {
        SpringApplication.run(HsasApplication.class, args);
    }
}
