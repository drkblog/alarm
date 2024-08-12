# Alarm ESP32 client

Client software for polling the Calendar server

## Tooling

1. Visual Studio Code
2. PlatformIO plugin
3. ESP-IDF plugin 5.2.x

## Configure the project

Create a file name `local_config.h` in the `main` directory with the following settings:

```
#define CONFIG_WIFI_SSID "desired-ssid"
#define CONFIG_WIFI_PASSWORD "password"
#define SERVER_HOSTNAME "hostname"
```

Replacing the values with the wireless network **SSID** and password and the hostname of the computer running the server.
The **hostname** must be resolvable through [mDNS](https://en.wikipedia.org/wiki/Multicast_DNS)

## Build

Build using the ESD-IDF tool

