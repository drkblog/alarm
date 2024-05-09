package ar.com.drk.alarm.server.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

@Slf4j
@Service
public class CalendarService {
  private static final String APPLICATION_NAME = "AlarmServerClient";
  public static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private NetHttpTransport httpTransport;
  private FileDataStoreFactory dataStoreFactory;
  private Credential credential;
  private File dataStore;
  private Calendar client;

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

  public String getEvents() {
    try {
      final CalendarList feed = client.calendarList().list().execute();
      return feed.getItems().toString();
    } catch (final IOException e) {
      log.error("Error getting events", e);
      throw new UncheckedIOException(e);
    }
  }
}
