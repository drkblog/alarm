package ar.com.drk.alarm.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calendar")
public class ServerController {

  @GetMapping("/status")
  public String status() {
    return "OK";
  }
}
