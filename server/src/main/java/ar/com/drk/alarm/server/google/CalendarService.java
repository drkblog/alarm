package ar.com.drk.alarm.server.google;

import ar.com.drk.alarm.server.ServerConfiguration;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CalendarService {
  private final TimeService timeService;
  private final GoogleCalendarClient googleCalendarClient;
  private final ServerConfiguration serverConfiguration;

  private Set<String> alreadySeenEvents = Set.of();

  public CalendarService(
      final TimeService timeService,
      final GoogleCalendarClient googleCalendarClient,
      final ServerConfiguration serverConfiguration
  ) {
    this.timeService = timeService;
    this.googleCalendarClient = googleCalendarClient;
    this.serverConfiguration = serverConfiguration;
  }

  public Boolean getEvents() {
    try {
      log.trace("Getting events...");
      final Events events = googleCalendarClient.getEvents(serverConfiguration.getCalendarId(), getRangeStart(), getRangeEnd());
      log.trace("Events received: {}", events);
      final boolean triggerAlarm = events.getItems().stream()
          .filter(this::isNew)
          .anyMatch(shouldTriggerAlarm());
      log.trace("Trigger alarm: {}", triggerAlarm);
      alreadySeenEvents = getIds(events);
      return triggerAlarm;
    } catch (final IOException e) {
      log.error("Error getting events", e);
      throw new UncheckedIOException(e);
    }
  }

  private boolean isNew(final Event event) {
    return !alreadySeenEvents.contains(event.getId());
  }

  private static Set<String> getIds(final Events events) {
    return events.getItems().stream().map(Event::getId).collect(Collectors.toSet());
  }

  // Which kind of events we care about
  private static Predicate<Event> shouldTriggerAlarm() {
    return event -> event.getStart().getDateTime() != null && !event.getStart().getDateTime().isDateOnly();
  }

  private DateTime getRangeEnd() {
    return new DateTime(timeService.now().plus(serverConfiguration.getTimeWindow()).toEpochMilli());
  }

  private DateTime getRangeStart() {
    return new DateTime(timeService.now().toEpochMilli());
  }
}
