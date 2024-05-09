package ar.com.drk.alarm.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ServerConfiguration.class)
public class ServerApplication {

  public static void main(final String[] args) {
    SpringApplication.run(ServerApplication.class, args);
  }

}
