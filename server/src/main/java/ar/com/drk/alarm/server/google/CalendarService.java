package ar.com.drk.alarm.server.google;

import ar.com.drk.alarm.server.ServerConfiguration;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Collections;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {
  private static final String APPLICATION_NAME = "AlarmServerClient";
  public static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

  private final ServerConfiguration configuration;

  private NetHttpTransport httpTransport;
  private FileDataStoreFactory dataStoreFactory;
  private Credential credential;
  private File dataStore;
  private Calendar client;
  private final TemporalAmount timeWindow = Duration.of(3, ChronoUnit.MINUTES);

  @PostConstruct
  public void initialize() {
    try {
      dataStore = new File(System.getProperty("user.home"), ".alarm-server/store");
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(dataStore);
      credential = authorize();
      log.info("Credential authorized: {}", credential);
      client = new Calendar.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Credential authorize() throws Exception {
    // load client secrets
    final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY,
        new InputStreamReader(Files.newInputStream(Paths.get("alarm-server-client-secret.json")))
    );
    // set up authorization code flow
    final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport,
        JSON_FACTORY,
        clientSecrets,
        Collections.singleton(CalendarScopes.CALENDAR)
    ).setDataStoreFactory(dataStoreFactory).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  public Boolean getEvents() {
    try {
      final Events events = client.events()
          .list(configuration.getCalendarId())
          .setTimeMin(getRangeStart())
          .setTimeMax(getRangeEnd())
          .execute();
      return events.getItems().stream()
          .anyMatch(shouldTriggerAlarm());
    } catch (final IOException e) {
      log.error("Error getting events", e);
      throw new UncheckedIOException(e);
    }
  }

  private static Predicate<Event> shouldTriggerAlarm() {
    return event -> event.getStart().getDateTime() != null && !event.getStart().getDateTime().isDateOnly();
  }

  private boolean isWithinRange(final Event event) {
    final long start = event.getEnd().getDateTime().getValue();
    return start > getRangeStart().getValue() && event.getStart().getDateTime().getValue() < getRangeEnd().getValue();
  }

  private DateTime getRangeEnd() {
    return new DateTime(Instant.now().plus(timeWindow).toEpochMilli());
  }

  private DateTime getRangeStart() {
    return new DateTime(Instant.now().toEpochMilli());
  }


}
