package ar.com.drk.alarm.server.google;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TimeService {
  public Instant now() {
    return Instant.now();
  }
}
