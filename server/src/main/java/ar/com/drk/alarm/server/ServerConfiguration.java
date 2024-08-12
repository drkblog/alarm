package ar.com.drk.alarm.server;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.temporal.TemporalAmount;

@Getter
@Validated
@ConfigurationProperties("alarm")
@RequiredArgsConstructor
public final class ServerConfiguration {
  @NotEmpty
  private final String calendarId;
  private final TemporalAmount timeWindow;
}
