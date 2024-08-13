package ar.com.drk.alarm.server;

import ar.com.drk.alarm.server.google.GoogleCalendarClient;
import ar.com.drk.alarm.server.google.TimeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
class ServerApplicationTests {
  private static final Instant NOW = Instant.now();

  @Autowired
  protected MockMvc mockMvc;
  @MockBean
  TimeService timeService;
  @MockBean
  private GoogleCalendarClient googleCalendarClient;

  @Test
  void givenNoEvents_whenCalendarStatus_thenFalse() throws Exception {
    when(timeService.now()).thenReturn(NOW);
    final Events events = new Events();
    events.setItems(List.of());
    when(googleCalendarClient.getEvents(any(), any(), any())).thenReturn(events);
    mockMvc.perform(get("/calendar/status"))
      .andExpect(status().isOk())
      .andExpect(content().string("false"));
  }
  
  @Test
  void givenOneEvent_whenCalendarStatus_thenTrue() throws Exception {
    when(timeService.now()).thenReturn(NOW);
    final Events events = new Events();
    final EventDateTime start = new EventDateTime().setDateTime(new DateTime(NOW.plusSeconds(1).toEpochMilli()));
    final Event event = new Event().setId("id").setStart(start);
    events.setItems(List.of(event));
    when(googleCalendarClient.getEvents(any(), any(), any())).thenReturn(events);
    mockMvc.perform(get("/calendar/status"))
      .andExpect(status().isOk())
      .andExpect(content().string("true"));
  }

}
