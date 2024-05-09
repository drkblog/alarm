package ar.com.drk.alarm.server;

import ar.com.drk.alarm.server.google.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class ServerController {

  private final CalendarService calendarService;

  @GetMapping(
      value = "/status",
      produces = "application/json"
  )
  public Boolean status() {
    return calendarService.getEvents();
  }
}
