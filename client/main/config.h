#include "esp_wifi_types.h"
#include "local_config.h"

// The local_config.h file must define:
// #define CONFIG_WIFI_SSID "desired-ssid"
// #define CONFIG_WIFI_PASSWORD "password"

#define WIFI_SCAN_METHOD WIFI_ALL_CHANNEL_SCAN
#define WIFI_CONNECT_AP_SORT_METHOD WIFI_CONNECT_AP_BY_SIGNAL
#define CONFIG_WIFI_SCAN_RSSI_THRESHOLD -127
#define WIFI_SCAN_AUTH_MODE_THRESHOLD WIFI_AUTH_OPEN

#define SERVER_HOSTNAME "***REMOVED***"
#define SERVER_PORT 2525

#define POLL_PERIOD_MS 3000
#define BLINK_PERIOD_MS 500
#define LOOP_PERIOD_MS 100
#define ALARM_OUTPUT_IO 8
