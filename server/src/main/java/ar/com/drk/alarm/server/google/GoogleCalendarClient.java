package ar.com.drk.alarm.server.google;

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
import com.google.api.services.calendar.model.Events;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleCalendarClient {
  private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String APPLICATION_NAME = "AlarmServerClient";
  private static final String CREDENTIALS_DIRECTORY = ".alarm-server/store";

  private NetHttpTransport httpTransport;
  private FileDataStoreFactory dataStoreFactory;
  private Credential credential;
  private File dataStore;
  private Calendar client;

  @PostConstruct
  public void initialize() {
    try {
      dataStore = new File(System.getProperty("user.home"), CREDENTIALS_DIRECTORY);
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
    final InputStream clientSecretResource = CalendarService.class.getResourceAsStream("/alarm-server-client-secret.json");
    if (clientSecretResource == null) {
      throw new IllegalStateException("Client secret not found");
    }
    final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY,
        new InputStreamReader(clientSecretResource)
    );
    final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport,
        JSON_FACTORY,
        clientSecrets,
        Collections.singleton(CalendarScopes.CALENDAR)
    ).setDataStoreFactory(dataStoreFactory).build();
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  public Events getEvents(final @NotEmpty String calendarId, final DateTime rangeStart, final DateTime rangeEnd) throws IOException {
    return client.events()
        .list(calendarId)
        .setTimeMin(rangeStart)
        .setTimeMax(rangeEnd)
        .execute();
  }
}
