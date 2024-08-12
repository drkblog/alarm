package ar.com.drk.alarm.server.google;

import ar.com.drk.alarm.server.ServerConfiguration;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Events;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {
  @Mock
  private TimeService timeService;
  @Mock
  private GoogleCalendarClient googleCalendarClient;
  @Mock
  private ServerConfiguration serverConfiguration;
  @InjectMocks
  private CalendarService calendarService;

  @Test
  void giveNoEvent_whenGetEvents_thenReturnFalse() throws IOException {
    // Given
    final Instant now = Instant.now();
    final String calendarId = "calendarId";
    final TemporalAmount timeWindow = Duration.of(1, ChronoUnit.MINUTES);
    final DateTime start = new DateTime(now.toEpochMilli());
    final DateTime end = new DateTime(now.plus(timeWindow).toEpochMilli());
    when(timeService.now()).thenReturn(now);
    when(serverConfiguration.getCalendarId()).thenReturn(calendarId);
    when(serverConfiguration.getTimeWindow()).thenReturn(timeWindow);
    final Events events = new Events();
    events.setItems(List.of());
    when(googleCalendarClient.getEvents(calendarId, start, end)).thenReturn(events);

    // When-Then
    assertThat(calendarService.getEvents()).isFalse();
  }
}