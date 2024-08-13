package ar.com.drk.alarm.server.google;

import ar.com.drk.alarm.server.ServerConfiguration;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    final Duration timeWindow = Duration.of(1, ChronoUnit.MINUTES);
    final DateTime start = new DateTime(now.toEpochMilli());
    final DateTime end = new DateTime(now.plus(timeWindow).toEpochMilli());
    when(timeService.now()).thenReturn(now);
    when(serverConfiguration.getCalendarId()).thenReturn(calendarId);
    when(serverConfiguration.getTimeWindow()).thenReturn(timeWindow);
    final Events events = getEvents(start, List.of());
    when(googleCalendarClient.getEvents(calendarId, start, end)).thenReturn(events);

    // When-Then
    assertThat(calendarService.getEvents()).isFalse();
  }

  @Test
  void giveOneEvent_whenGetEvents_thenReturnTrue() throws IOException {
    // Given
    final Instant now = Instant.now();
    final String calendarId = "calendarId";
    final Duration timeWindow = Duration.of(1, ChronoUnit.MINUTES);
    final DateTime start = new DateTime(now.toEpochMilli());
    final DateTime end = new DateTime(now.plus(timeWindow).toEpochMilli());
    when(timeService.now()).thenReturn(now);
    when(serverConfiguration.getCalendarId()).thenReturn(calendarId);
    when(serverConfiguration.getTimeWindow()).thenReturn(timeWindow);

    final Events events = getEvents(start, List.of(Duration.of(10, ChronoUnit.SECONDS)));
    when(googleCalendarClient.getEvents(calendarId, start, end)).thenReturn(events);

    // When-Then
    assertThat(calendarService.getEvents()).isTrue();
    verify(googleCalendarClient, times(1)).getEvents(calendarId, start, end);
  }

  @Test
  void giveTwoEvents_whenGetEvents_thenReturnTrue() throws IOException {
    // Given
    final Instant now = Instant.now();
    final String calendarId = "calendarId";
    final Duration timeWindow = Duration.of(1, ChronoUnit.MINUTES);
    final DateTime start = new DateTime(now.toEpochMilli());
    final DateTime end = new DateTime(now.plus(timeWindow).toEpochMilli());
    when(timeService.now()).thenReturn(now);
    when(serverConfiguration.getCalendarId()).thenReturn(calendarId);
    when(serverConfiguration.getTimeWindow()).thenReturn(timeWindow);

    final Events events = getEvents(start, List.of(
        Duration.of(10, ChronoUnit.SECONDS),
        Duration.of(20, ChronoUnit.SECONDS)
    ));
    when(googleCalendarClient.getEvents(calendarId, start, end)).thenReturn(events);

    // When-Then
    assertThat(calendarService.getEvents()).isTrue();
    verify(googleCalendarClient, times(1)).getEvents(calendarId, start, end);
  }

  @Test
  void giveOneEventWithNoStart_whenGetEvents_thenReturnFalse() throws IOException {
    // Given
    final Instant now = Instant.now();
    final String calendarId = "calendarId";
    final Duration timeWindow = Duration.of(1, ChronoUnit.MINUTES);
    final DateTime start = new DateTime(now.toEpochMilli());
    final DateTime end = new DateTime(now.plus(timeWindow).toEpochMilli());
    when(timeService.now()).thenReturn(now);
    when(serverConfiguration.getCalendarId()).thenReturn(calendarId);
    when(serverConfiguration.getTimeWindow()).thenReturn(timeWindow);

    final Events events = getEvents(start, List.of(Duration.of(10, ChronoUnit.SECONDS)));
    events.getItems().get(0).getStart().setDateTime(null);
    when(googleCalendarClient.getEvents(calendarId, start, end)).thenReturn(events);

    // When-Then
    assertThat(calendarService.getEvents()).isFalse();
    verify(googleCalendarClient, times(1)).getEvents(calendarId, start, end);
  }

  @Test
  void giveOneEvent_whenGetEventsTwice_thenReturnTrueAndThenFalse() throws IOException {
    // Given
    final Instant now = Instant.now();
    final String calendarId = "calendarId";
    final Duration timeWindow = Duration.of(1, ChronoUnit.MINUTES);
    final DateTime start = new DateTime(now.toEpochMilli());
    final DateTime end = new DateTime(now.plus(timeWindow).toEpochMilli());
    when(timeService.now()).thenReturn(now);
    when(serverConfiguration.getCalendarId()).thenReturn(calendarId);
    when(serverConfiguration.getTimeWindow()).thenReturn(timeWindow);

    final Events events = getEvents(start, List.of(Duration.of(10, ChronoUnit.SECONDS)));
    when(googleCalendarClient.getEvents(calendarId, start, end)).thenReturn(events);

    // First time true
    assertThat(calendarService.getEvents()).isTrue();
    // Second time false
    assertThat(calendarService.getEvents()).isFalse();
  }

  @Test
  void giveService_whenGetEventsException_thenUncheckedIOException() throws IOException {
    // Given
    final Instant now = Instant.now();
    final String calendarId = "calendarId";
    final Duration timeWindow = Duration.of(1, ChronoUnit.MINUTES);
    final DateTime start = new DateTime(now.toEpochMilli());
    final DateTime end = new DateTime(now.plus(timeWindow).toEpochMilli());
    when(timeService.now()).thenReturn(now);
    when(serverConfiguration.getCalendarId()).thenReturn(calendarId);
    when(serverConfiguration.getTimeWindow()).thenReturn(timeWindow);
    when(googleCalendarClient.getEvents(calendarId, start, end)).thenThrow(new IOException());

    // False on error
    assertThatThrownBy(() -> calendarService.getEvents()).isInstanceOf(UncheckedIOException.class);
    verify(googleCalendarClient, times(1)).getEvents(calendarId, start, end);
  }

  private static Events getEvents(final DateTime start, List<TemporalAmount> startTimes) {
    final List<Event> items = new ArrayList<>(startTimes.size());
    for (int i = 0; i < startTimes.size(); i++) {
      items.add(new Event().setId("id" + i).setStart(createDateTime(start, startTimes.get(i))));
    }
    final Events events = new Events();
    events.setItems(items);
    return events;
  }

  private static EventDateTime createDateTime(final DateTime start, final TemporalAmount delta) {
    final Instant instant = Instant.ofEpochMilli(start.getValue()).plus(delta);
    return new EventDateTime().setDateTime(new DateTime(instant.toEpochMilli()));
  }
}